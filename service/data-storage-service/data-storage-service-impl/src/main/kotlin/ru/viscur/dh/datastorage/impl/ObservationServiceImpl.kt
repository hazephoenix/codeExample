package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.digitalhospital.dhdatastorage.dto.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import java.lang.Error

@Service
class ObservationServiceImpl(
        private val resourceService: ResourceService
) : ObservationService {

    /**
     * Найти все обследования по id пациента и статусу обследования
     */
    override fun findByPatient(patientId: String, status: ObservationStatus): List<Observation?> =
        resourceService.all(
            ResourceType.Observation,
            RequestBodyForResources(
                filter = mapOf("status" to status.name, "subject" to "Patient/$patientId")
            )
        )

    /**
     * Зарегистрировать обследование
     *
     * Обследование обязательно должно содержать поле basedOn
     * со ссылкой на ServiceRequest
     */
    override fun create(observation: Observation): Observation? {
        updateServiceRequestStatus(observation)
        return resourceService.create(observation)
    }

    /**
     * Обновить обследование (добавить результаты) и соответствующее направление
     */
    override fun update(observation: Observation): Observation? {
        val storedObservation = resourceService.byId(ResourceType.Observation, observation.id)
        updateServiceRequestStatus(storedObservation)
        return resourceService.update(observation)
    }

    /**
     * Обновить статус направления на обследование
     */
    private fun updateServiceRequestStatus(observation: Observation) =
            observation.basedOn?.id?.let { serviceRequestId ->
                resourceService.byId(ResourceType.ServiceRequest, serviceRequestId)
                        .let {
                            when (observation.status) {
                                ObservationStatus.registered -> it.status = ServiceRequestStatus.waiting_result
                                ObservationStatus.final -> it.status = ServiceRequestStatus.completed
                                else -> it.status
                            }
                            resourceService.update(it)
                        }
            } ?: throw Error("No correct ServiceRequest.id provided (basedOn)")
}