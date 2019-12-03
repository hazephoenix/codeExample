package ru.viscur.dh.integration.doctorapp.impl

import org.springframework.context.ApplicationEventPublisher
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
import ru.viscur.dh.integration.doctorapp.api.model.QueuePatient
import ru.viscur.dh.integration.doctorapp.impl.mapper.DoctorAppMapper
import java.util.*

@Component
class DoctorAppEventPublisherImpl(
        val eventPublisher: ApplicationEventPublisher,
        val doctorAppMapper: DoctorAppMapper,
        val doctorMessageService: DoctorMessageService,
        val resourceService: ResourceService

) : DoctorAppEventPublisher {
    override fun publishDoctorCreated(practitioner: Practitioner) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorNewEvent(
                                doctorAppMapper.mapPractitionerToCallableDoctor(practitioner)
                        )
                )
        )
    }

    override fun publishDoctorRemoved(practitionerId: String) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorLostEvent(practitionerId)
                )
        )
    }

    override fun publishDoctorStatusChanged(practitionerId: String, disabled: Boolean) {
        // отправляем клиентам чтобы обновили UI, кэш
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        CallableDoctorStatusChangedEvent(practitionerId, disabled)
                )
        )
    }

    override fun publishNewQueuePatient(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression, patient: QueuePatient) {
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        QueuePatientNewEvent(
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
                            MessageNewEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishQueuePatientRemoved(targetPractitionersIds: Set<String>, patientId: String) {
        eventPublisher.publishEvent(
                DoctorAppEvent(
                        QueuePatientLostEvent(patientId),
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
                            MessageNewEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }

    override fun publishObservationReady(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression) {
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
                            MessageNewEvent(
                                    doctorAppMapper.mapMessage(message)
                            )
                    )
            )
        }
    }
}