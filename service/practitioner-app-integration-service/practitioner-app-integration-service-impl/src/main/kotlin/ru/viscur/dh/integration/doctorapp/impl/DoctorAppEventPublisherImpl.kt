package ru.viscur.dh.integration.doctorapp.impl

import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.DoctorMessageService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.model.message.DoctorMessage
import ru.viscur.dh.datastorage.api.model.message.DoctorMessageType
import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.integration.doctorapp.api.DoctorAppEventPublisher
import ru.viscur.dh.integration.doctorapp.api.event.*
import ru.viscur.dh.integration.doctorapp.api.model.QueuePatientAppDto
import ru.viscur.dh.integration.doctorapp.impl.mapper.DoctorAppMapper
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallAcceptedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallCreatedEvent
import ru.viscur.dh.practitioner.call.api.event.PractitionerCallDeclinedEvent
import java.util.*

@Component
class DoctorAppEventPublisherImpl(
        val eventPublisher: ApplicationEventPublisher,
        val doctorAppMapper: DoctorAppMapper,
        val doctorMessageService: DoctorMessageService,
        val resourceService: ResourceService

) : DoctorAppEventPublisher {
    override fun publishPractitionerCreated(practitioner: Practitioner) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorNewAppEvent(
                                doctorAppMapper.mapPractitionerToCallableDoctor(practitioner)
                        )
                )
        )
    }

    override fun publishPractitionerRemoved(practitionerId: String) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorLostAppEvent(practitionerId)
                )
        )
    }

    override fun publishPractitionerStatusChanged(practitionerId: String, disabled: Boolean) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorStatusChangedAppEvent(practitionerId, disabled)
                )
        )
    }

    override fun publishNewQueuePatient(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression, patient: QueuePatientAppDto) {
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        QueuePatientNewAppEvent(
                                patient
                        ),
                        targetPractitionersIds
                )
        )
        targetPractitionersIds.forEach {
            val message = doctorMessageService.createMessage(DoctorMessage(
                    genId(),
                    Date(),
                    resourceService.byId(ResourceType.Practitioner, it),
                    clinicalImpression,
                    DoctorMessageType.NewPatient,
                    false
            ))
            eventPublisher.publishEvent(
                    DoctorAppEvent(
                            MessageNewAppEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishQueuePatientRemoved(targetPractitionersIds: Set<String>, patientId: String) {
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        QueuePatientLostAppEvent(patientId),
                        targetPractitionersIds
                )
        )
    }

    override fun publishPatientServiceTimeElapsed(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression) {
        val practitioners = resourceService.classifiedByIds(ResourceType.Practitioner, targetPractitionersIds)
        targetPractitionersIds.forEach {
            val practitioner = practitioners[it] ?: return@forEach
            val message = doctorMessageService.createMessage(DoctorMessage(
                    genId(),
                    Date(),
                    practitioner,
                    clinicalImpression,
                    DoctorMessageType.ServiceTimeElapsed,
                    false
            ))
            eventPublisher.publishEvent(
                    DoctorAppEvent(
                            MessageNewAppEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishObservationsResultsAreReady(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression) {
        val practitioners = resourceService.classifiedByIds(ResourceType.Practitioner, targetPractitionersIds)
        targetPractitionersIds.forEach {
            val practitioner = practitioners[it] ?: return@forEach
            val message = doctorMessageService.createMessage(DoctorMessage(
                    genId(),
                    Date(),
                    practitioner,
                    clinicalImpression,
                    DoctorMessageType.ObservationReady,
                    false
            ))
            eventPublisher.publishEvent(
                    DoctorAppEvent(
                            MessageNewAppEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }


    @EventListener
    fun routePractitionerCallCreatedEvent(event: PractitionerCallCreatedEvent) {
        eventPublisher.publishEvent(DoctorAppEvent(
                PractitionerCallCreatedAppEvent(
                        doctorAppMapper.mapPractitionerCallToApp(event.call)
                )
        ))
    }

    @EventListener
    fun routePractitionerCallAcceptedEvent(event: PractitionerCallAcceptedEvent) {
        eventPublisher.publishEvent(DoctorAppEvent(
                PractitionerCallAcceptedEvent(
                        event.callId,
                        event.timeToArrival
                )
        ))
    }

    @EventListener
    fun routePractitionerCallDeclinedEvent(event: PractitionerCallDeclinedEvent) {
        eventPublisher.publishEvent(DoctorAppEvent(
                PractitionerCallDeclinedAppEvent(
                        event.callId
                )
        ))
    }

}