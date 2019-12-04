package ru.viscur.dh.practitioner.call.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.DependsOn
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PractitionerCallStorageService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.practitioner.call.api.PractitionerCallService
import ru.viscur.dh.practitioner.call.api.cmd.AcceptPractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.CommandInitiator
import ru.viscur.dh.practitioner.call.api.cmd.CreatePractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.cmd.DeclinePractitionerCallCmd
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallAcceptedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallCreatedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallDeclinedEvent
import ru.viscur.dh.practitioner.call.api.exception.PractitionerCallWrongStatusException
import ru.viscur.dh.practitioner.call.model.AwaitingCallStatusStage
import ru.viscur.dh.practitioner.call.model.AwaitingPractitionerCallRef
import ru.viscur.dh.practitioner.call.model.CallStatus
import ru.viscur.dh.practitioner.call.model.PractitionerCall
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct

@Service
@Order(Ordered.LOWEST_PRECEDENCE)

@DependsOn("dsFlyway")
class PractitionerCallServiceImpl(
        val practitionerService: PractitionerService,
        val practitionerCallStorageService: PractitionerCallStorageService,
        val locationService: LocationService,
        val eventPublisher: ApplicationEventPublisher
) : PractitionerCallService {

    /**
     * Ожидающие вызовы
     */
    private val awaitingRefs = ConcurrentHashMap<String, AwaitingPractitionerCallRefEntry>()


    @PostConstruct
    private fun postConstruct() {
        practitionerCallStorageService.getAllAwaitingRef()
                .forEach {
                    var call = practitionerCallStorageService.byId(it.callId)
                    call.status = CallStatus.Declined
                    call = practitionerCallStorageService.updateCall(call)
                    eventPublisher.publishEvent(PractitionerCallDeclinedEvent(call.id))
                }
    }

    override fun byId(id: String): PractitionerCall {
        return practitionerCallStorageService.byId(id)
    }


    @Tx
    override fun createCall(cmd: CreatePractitionerCallCmd): PractitionerCall {
        val call = practitionerCallStorageService.createCall(
                PractitionerCall(
                        dateTime = Date(),
                        caller = practitionerService.byId(cmd.callerId),
                        specializationCategory = cmd.specializationCategory,
                        practitioner = practitionerService.byId(cmd.practitionerId),
                        goal = cmd.goal,
                        patientSeverity = cmd.patientSeverity,
                        location = locationService.byId(cmd.locationId),
                        comment = cmd.comment,
                        status = CallStatus.Awaiting,
                        timeToArrival = null
                )
        )
        val ref =
                AwaitingPractitionerCallRef(
                        call.id, call.dateTime, AwaitingCallStatusStage.PractitionerAppCall
                )
        practitionerCallStorageService.createAwaitingRef(ref)
        awaitingRefs[call.id] = AwaitingPractitionerCallRefEntry(
                practitionerCallStorageService,
                awaitingRefs,
                ref
        )
        // TODO: вызывать после коммита транзакции???
        eventPublisher.publishEvent(
                PractitionerCallCreatedEvent(call)
        )
        return call
    }

    override fun acceptCall(cmd: AcceptPractitionerCallCmd): PractitionerCall {
        val call = synchronizedExecuteIfAwaiting(cmd.callId) {
            var call = practitionerCallStorageService.byId(cmd.callId)
            call.status = CallStatus.Accepted
            call.timeToArrival = cmd.timeToArrival

            call = practitionerCallStorageService.updateCall(call)
            notAwaitingAnymore()
            return@synchronizedExecuteIfAwaiting call
        }
        eventPublisher.publishEvent(
                PractitionerCallAcceptedEvent(
                        call.id,
                        call.timeToArrival!!
                )
        )
        return call
    }

    override fun declineCall(cmd: DeclinePractitionerCallCmd): PractitionerCall {
        val call = synchronizedExecuteIfAwaiting(cmd.callId) {
            var call = practitionerCallStorageService.byId(cmd.callId)
            call.status = CallStatus.Declined
            call = practitionerCallStorageService.updateCall(call)
            notAwaitingAnymore()
            return@synchronizedExecuteIfAwaiting call
        }
        eventPublisher.publishEvent(PractitionerCallDeclinedEvent(call.id))
        return call
    }

    /**
     * Выполняет указанный блок кода если есть вызов со статусом "Ожидает".
     *
     * Выполнение синхронизированное, на момент вызова block, гарантируется что:
     *  * block - единственный код который в данный момент работает для указанного вызова
     *  * вызов с callId еще небыл принят или отклонен
     *
     *  @param callId ID вызова
     *  @param block функция для выполнения
     *  @throws PractitionerCallWrongStatusException в случае, если вызов был
     *             принят или отклонен раньше чем вызван [block]
     */
    private fun <T> synchronizedExecuteIfAwaiting(callId: String, block: AwaitingPractitionerCallRefEntry.() -> T): T {
        val awaitingRef = awaitingRef(callId)
        synchronized(awaitingRef) {
            if (awaitingRef.awaiting) {
                // Все еще ожидаем
                return awaitingRef.block()
            } else {
                throw PractitionerCallWrongStatusException(
                        CallStatus.Awaiting,
                        practitionerCallStorageService.byId(callId)
                )
            }
        }
    }

    /**
     * Запускаем каждые 40 секунд и продвигаем ожидающие вызовы по стадиям ожидвния
     */
    @Scheduled(fixedDelay = 40_000)
    protected fun handleAwaitingCalls() {
        val items = awaitingRefs.values.toList()
        for (awaitingRef in items) {
            if (!awaitingRef.awaiting) {
                continue
            }
            synchronized(awaitingRef) {
                if (!awaitingRef.awaiting) {
                    // Пока захватывали монитор, уже все, ничего не надо делать ...
                    return@synchronized
                }
                // Все еще ждем ответа
                val ref = awaitingRef.ref
                val timeFromLastAction = System.currentTimeMillis() - ref.lastStageDateTime.time
                if (ref.awaitingCallStatusStage == AwaitingCallStatusStage.VoiceCall &&
                        ref.voiceCallCount == MAX_VOICE_CALL_COUNT) {
                    // Уже вызвали максималное количество раз голосом
                    if (timeFromLastAction >= DELAY_BEFORE_AUTO_DECLINE) {
                        // время ожидания после последнего вызова окончено,
                        // отменяем автоматически
                        declineCall(
                                DeclinePractitionerCallCmd(
                                        ref.callId,
                                        CommandInitiator.System
                                )
                        )
                    }
                } else {
                    // Еще можем сделать вызов голосом
                    if (timeFromLastAction >= DELAY_BEFORE_VOICE_CALL) {
                        // Время вызова пришло, вызовим
                        //TODO Do voice call
                        ref.awaitingCallStatusStage = AwaitingCallStatusStage.VoiceCall
                        ref.voiceCallCount = (ref.voiceCallCount ?: 0) + 1
                        ref.lastStageDateTime = Date()
                        practitionerCallStorageService.updateAwaitingRef(ref)
                    }
                }
            }
        }
    }


    /**
     * Блокирует вызов.
     *
     * Все методы, которые влияют на стадию ожидания вызова должны блокировать вызовы в момент,
     * когда они работают над ним
     */
    private fun awaitingRef(id: String): AwaitingPractitionerCallRefEntry {
        return awaitingRefs[id]
                ?: throw PractitionerCallWrongStatusException(
                        CallStatus.Awaiting,
                        practitionerCallStorageService.byId(id)
                )
    }

    private class AwaitingPractitionerCallRefEntry(
            val practitionerCallStorageService: PractitionerCallStorageService,
            val owner: ConcurrentHashMap<String, AwaitingPractitionerCallRefEntry>,
            val ref: AwaitingPractitionerCallRef
    ) {
        @Volatile
        var awaiting = true

        fun notAwaitingAnymore() {
            awaiting = false
            owner.remove(ref.callId)
            practitionerCallStorageService.removeAwaitingRef(ref.callId)
        }
    }

    companion object {
        const val MAX_VOICE_CALL_COUNT = 2
        const val DELAY_BEFORE_VOICE_CALL = 30 * 1000
        const val DELAY_BEFORE_AUTO_DECLINE = 60 * 1000
    }

}