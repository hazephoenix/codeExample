package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.util.BLOOD_ANALYSIS_CATEGORY
import ru.viscur.dh.datastorage.api.util.URINE_ANALYSIS_CATEGORY
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.util.*
import javax.persistence.*

@Service
class ObservationServiceImpl(
        private val resourceService: ResourceService,
        private val serviceRequestService: ServiceRequestService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val conceptService: ConceptService
) : ObservationService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun byPeriod(start: Date, end: Date): List<Observation> {
        val query = em.createNativeQuery("""
            select r.resource
            from Observation r
            where r.resource->'basedOn' <> 'null' 
              and (r.resource->>'issued')\:\:bigint >= :periodStart
              and (r.resource->>'issued')\:\:bigint <= :periodEnd
            order by r.resource ->> 'issued'
            """)
        query.setParameter("periodStart", start.time)
        query.setParameter("periodEnd", end.time)
        return query.fetchResourceList()
    }

    /**
     * Найти все обследования по id пациента и статусу обследования
     */
    override fun byPatientAndStatus(patientId: String, status: ObservationStatus?): List<Observation> {
        var queryStr = """
            select r.resource
                from Observation r
                where r.resource -> 'basedOn' ->> 'reference' in (
                    select
                        jsonb_array_elements(cp.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                    from CarePlan cp
                    where 'CarePlan/' || cp.id in (
                        select jsonb_array_elements(ci.resource -> 'supportingInfo') ->> 'reference'
                        from clinicalImpression ci
                        where ci.resource -> 'subject' ->> 'reference' = ?1
                          and ci.resource ->> 'status' = 'active'
                    )
                )                
        """
        val params = mutableListOf("Patient/$patientId")
        status?.run {
            queryStr += "\nand r.resource ->> 'status' = ?2"
            params += status.toString()
        }
        val query = em.createNativeQuery(queryStr)
        query.setParameters(params)
        return query.fetchResourceList()
    }

    override fun byBaseOnServiceRequestId(id: String): Observation? {
        val query = em.createNativeQuery("""
            select r.resource
                from Observation r
                where r.resource -> 'basedOn' ->> 'reference' = :serviceRequestRef
        """)
        query.setParameter("serviceRequestRef", "ServiceRequest/$id")
        return query.fetchResourceList<Observation>().firstOrNull()
    }

    override fun start(serviceRequestId: String) {
        resourceService.update(ResourceType.ServiceRequest, serviceRequestId) {
            extension = extension?.apply { execStart = now() }
                    ?: ServiceRequestExtension(execStart = now())
        }
    }

    /**
     * Зарегистрировать обследование
     *
     * Обследование обязательно должно содержать поле basedOn
     * со ссылкой на ServiceRequest
     */
    override fun create(patientId: String, observation: Observation, diagnosis: String?, severity: Severity): Observation {
        observation.subject = referenceToPatient(patientId)
        observation.basedOn?.id?.run {
            val updatedServiceRequest = resourceService.update(ResourceType.ServiceRequest, this) {
                extension = extension?.apply { execEnd = now() }
                        ?: ServiceRequestExtension(execEnd = now())
                observation.code = this.code
            }
            if (diagnosis != null && updatedServiceRequest.extension?.execStart != null) {
                observationDurationService.saveToHistory(patientId, updatedServiceRequest.code.code(), diagnosis, severity, updatedServiceRequest.extension!!.execStart!!, updatedServiceRequest.extension!!.execEnd!!)
            }
        } ?: throw Exception("not defined serviceRequestId in basedOn of observation")
        //если это кровь, то необходимо автоматом сделать прием мочи
        //todo пред. версия. если не вернемся к ней, можно удалить
//        val observationTypeConcept = conceptService.byCode(ValueSetName.OBSERVATION_TYPES.id, observation.code.code())
//        if (observationTypeConcept.parentCode == BLOOD_ANALYSIS_CATEGORY) {
//            val urineServiceRequests = serviceRequestService.activeByObservationCategory(patientId, URINE_ANALYSIS_CATEGORY)
//            urineServiceRequests.forEach {
//                create(patientId, Observation(
//                        code = it.code,
//                        subject = observation.subject,
//                        performer = listOf(),
//                        basedOn = Reference(it),
//                        issued = now()
//                ), diagnosis, severity)
//            }
//        }
        updateRelated(patientId, observation)
        return resourceService.create(observation)
    }

    /**
     * Обновить обследование (добавить результаты)
     */
    override fun update(patientId: String, observation: Observation): Observation {
        val updatedObservation = resourceService.update(ResourceType.Observation, observation.id) {
            performer = (performer + observation.performer).distinctBy { item -> item.id }
            status = observation.status
            valueBoolean = observation.valueBoolean
            valueInteger = observation.valueInteger
            valueQuantity = observation.valueQuantity
            valueSampledData = observation.valueSampledData
            valueString = observation.valueString
        }
        updateRelated(patientId, updatedObservation)
        return updatedObservation
    }

    override fun cancelByServiceRequests(patientId: String, officeId: String) {
        val activeInOffice = serviceRequestService.active(patientId, officeId)
        activeInOffice.forEach {
            cancelByBaseOnServiceRequestId(it.id)
        }
    }

    override fun cancelByBaseOnServiceRequestId(id: String) {
        byBaseOnServiceRequestId(id)?.run {
            if (status == ObservationStatus.registered) {
                resourceService.update(ResourceType.Observation, this.id) {
                    status = ObservationStatus.cancelled
                }
            }
        }
    }

    /**
     * Обновить связанные ресурсы -
     *  статус направления на обследование и маршрутного листа
     */
    private fun updateRelated(patientId: String, observation: Observation) {
        // Обновить статус направления на обследование
        val updatedServiceRequest = serviceRequestService.updateStatusByObservation(observation)
        // Обновить статус маршрутного листа
        updateCarePlan(patientId, updatedServiceRequest.id)
    }


    /**
     * Обновить соответствующий направлению [ServiceRequest] маршрутный лист [CarePlan]
     */
    private fun updateCarePlan(patientId: String, serviceRequestId: String) {
        getCarePlanByServiceRequestId(serviceRequestId)?.let { carePlan ->
            resourceService.update(ResourceType.CarePlan, carePlan.id) {
                val serviceRequests = serviceRequestService.all(patientId)
                val serviceRequestsWithoutResp = serviceRequests.filter { !it.isInspectionOfResp() }
                status = when {
                    serviceRequestsWithoutResp.any { it.status == ServiceRequestStatus.active } -> CarePlanStatus.active
                    serviceRequestsWithoutResp.all { it.status == ServiceRequestStatus.completed } -> CarePlanStatus.results_are_ready
                    else -> CarePlanStatus.waiting_results
                }
            }
        }
    }

    /**
     * Получить [CarePlan] по id [ServiceRequest]
     */
    private fun getCarePlanByServiceRequestId(serviceRequestId: String): CarePlan? {
        val query = em.createNativeQuery("""
                select r.resource
                from CarePlan r
                where 'ServiceRequest/' || :servReqId in (
                    select
                        jsonb_array_elements(rIntr.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                    from CarePlan rIntr
                    where rIntr.id = r.id
                )
        """)
        query.setParameter("servReqId", serviceRequestId)
        return query.fetchResource()
    }

    /**
     * Находит в CarePlan.activity незавершенные ServiceRequest
     */
    fun getUncompletedServiceRequests(carePlanId: String): List<ServiceRequest> {
        val query = em.createNativeQuery("""
            select sr.resource from ServiceRequest sr
            where sr.resource ->>'status' != 'completed' and 'ServiceRequest/' || sr.id in (
                select
                    jsonb_array_elements(rIntr.resource -> 'activity') -> 'outcomeReference' ->> 'reference'
                from CarePlan rIntr
                where rIntr.id = :carePlanId
            )
        """)
        query.setParameter("carePlanId", carePlanId)
        return query.fetchResourceList()
    }
}