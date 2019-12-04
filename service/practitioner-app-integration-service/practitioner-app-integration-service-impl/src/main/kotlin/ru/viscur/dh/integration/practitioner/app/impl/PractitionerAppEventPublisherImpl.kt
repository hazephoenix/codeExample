package ru.viscur.dh.integration.practitioner.app.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.PractitionerMessageService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.model.message.PractitionerMessage
import ru.viscur.dh.datastorage.api.model.message.PractitionerMessageType
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.integration.practitioner.app.api.PractitionerAppEventPublisher
import ru.viscur.dh.integration.practitioner.app.api.event.*
import ru.viscur.dh.integration.practitioner.app.api.model.QueuePatientAppDto
import ru.viscur.dh.integration.practitioner.app.impl.mapper.PractitionerAppMapper
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallAcceptedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallCreatedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallDeclinedEvent
import java.util.*

@Component
class PractitionerAppEventPublisherImpl(
        val eventPublisher: ApplicationEventPublisher,
        val practitionerAppMapper: PractitionerAppMapper,
        val practitionerMessageService: PractitionerMessageService,
        val resourceService: ResourceService

) : PractitionerAppEventPublisher {
    override fun publishPractitionerCreated(practitioner: Practitioner) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                PractitionerAppEvent(
                        CallablePractitionerNewAppEvent(
                                practitionerAppMapper.mapPractitionerToApp(practitioner)
                        )
                )
        )
    }

    override fun publishPractitionerRemoved(practitionerId: String) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                PractitionerAppEvent(
                        CallablePractitionerLostAppEvent(practitionerId)
                )
        )
    }

    override fun publishPractitionerStatusChanged(practitionerId: String, disabled: Boolean) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                PractitionerAppEvent(
                        CallablePractitionerStatusChangedAppEvent(practitionerId, disabled)
                )
        )
    }

    override fun publishNewQueuePatient(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression, patient: QueuePatientAppDto) {
        eventPublisher.publishEvent(
                PractitionerAppEvent(
                        QueuePatientNewAppEvent(
                                patient
                        ),
                        targetPractitionersIds
                )
        )
        targetPractitionersIds.forEach {
            val message = practitionerMessageService.createMessage(PractitionerMessage(
                    genId(),
                    Date(),
                    resourceService.byId(ResourceType.Practitioner, it),
                    clinicalImpression,
                    PractitionerMessageType.NewPatient,
                    false
            ))
            eventPublisher.publishEvent(
                    PractitionerAppEvent(
                            MessageNewAppEvent(
                                    practitionerAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishQueuePatientRemoved(targetPractitionersIds: Set<String>, patientId: String) {
        eventPublisher.publishEvent(
                PractitionerAppEvent(
                        QueuePatientLostAppEvent(patientId),
                        targetPractitionersIds
                )
        )
    }

    override fun publishPatientServiceTimeElapsed(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression) {
        val practitioners = resourceService.classifiedByIds(ResourceType.Practitioner, targetPractitionersIds)
        targetPractitionersIds.forEach {
            val practitioner = practitioners[it] ?: return@forEach
            val message = practitionerMessageService.createMessage(PractitionerMessage(
                    genId(),
                    Date(),
                    practitioner,
                    clinicalImpression,
                    PractitionerMessageType.ServiceTimeElapsed,
                    false
            ))
            eventPublisher.publishEvent(
                    PractitionerAppEvent(
                            MessageNewAppEvent(
                                    practitionerAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishObservationsResultsAreReady(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression) {
        val practitioners = resourceService.classifiedByIds(ResourceType.Practitioner, targetPractitionersIds)
        targetPractitionersIds.forEach {
            val practitioner = practitioners[it] ?: return@forEach
            val message = practitionerMessageService.createMessage(PractitionerMessage(
                    genId(),
                    Date(),
                    practitioner,
                    clinicalImpression,
                    PractitionerMessageType.ObservationsReady,
                    false
            ))
            eventPublisher.publishEvent(
                    PractitionerAppEvent(
                            MessageNewAppEvent(
                                    practitionerAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }


    @EventListener
    fun routePractitionerCallCreatedEvent(event: PractitionerCallCreatedEvent) {
        eventPublisher.publishEvent(PractitionerAppEvent(
                PractitionerCallCreatedAppEvent(
                        practitionerAppMapper.mapPractitionerCallToApp(event.call)
                )
        ))
    }

    @EventListener
    fun routePractitionerCallAcceptedEvent(event: PractitionerCallAcceptedEvent) {
        eventPublisher.publishEvent(PractitionerAppEvent(
                PractitionerCallAcceptedAppEvent(
                        event.callId,
                        event.timeToArrival
                )
        ))
    }

    @EventListener
    fun routePractitionerCallDeclinedEvent(event: PractitionerCallDeclinedEvent) {
        eventPublisher.publishEvent(PractitionerAppEvent(
                PractitionerCallDeclinedAppEvent(
                        event.callId
                )
        ))
    }

}