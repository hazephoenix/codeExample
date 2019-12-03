package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.DoctorCallService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.exception.EntityNotFoundException
import ru.viscur.dh.datastorage.api.model.call.DoctorCall
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.criteria.DoctorCallCriteriaQueryBuilder
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.math.ceil

@Service
class DoctorCallServiceImpl(
        val resourceService: ResourceService,
        val doctorCallCriteriaQueryBuilder: DoctorCallCriteriaQueryBuilder
) : DoctorCallService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    @Tx
    override fun byId(id: String): DoctorCall {
        val entity = em.find(
                ru.viscur.dh.datastorage.impl.entity.DoctorCall::class.java,
                id
        ) ?: throw EntityNotFoundException(id)
        return toApi(entity, null, null)
    }

    @Tx
    override fun createDoctorCall(call: DoctorCall): DoctorCall {
        val entity = ru.viscur.dh.datastorage.impl.entity.DoctorCall(
                id = genId(),
                dateTime = call.dateTime,
                callerId = call.caller.id,
                qualification = call.specialization,
                doctorId = call.doctor.id,
                goal = call.goal,
                patientSeverity = call.patientSeverity,
                locationId = call.location.id,
                comment = call.comment,
                status = call.status,
                timeToArrival = call.timeToArrival
        )
        em.persist(entity)
        return toApi(entity, null, null)
    }

    @Tx
    override fun updateDoctorCall(call: DoctorCall): DoctorCall {
        val entity = em.find(
                ru.viscur.dh.datastorage.impl.entity.DoctorCall::class.java,
                call.id
        ) ?: throw EntityNotFoundException(call.id)
        entity.callerId = call.caller.id
        entity.qualification = call.specialization
        entity.doctorId = call.doctor.id
        entity.goal = call.goal
        entity.patientSeverity = call.patientSeverity
        entity.locationId = call.location.id
        entity.comment = call.comment
        entity.status = call.status
        entity.timeToArrival = call.timeToArrival
        return toApi(entity, null, null)
    }


    override fun findCalls(request: PagedCriteriaRequest<DoctorCallCriteria>): PagedResponse<DoctorCall> {
        val requestCriteria = request.criteria
        val countCriteria = doctorCallCriteriaQueryBuilder
                .buildTotalCountQuery(requestCriteria)
        val dataCriteria = doctorCallCriteriaQueryBuilder
                .buildQuery(requestCriteria)

        val pageSize = request.pageSize ?: 20

        val totalCount = em.createQuery(countCriteria)
                .singleResult
        val data = em.createQuery(dataCriteria)
                .apply {
                    firstResult = request.page * pageSize
                    maxResults = pageSize
                }
                .resultList

        val practitionerIds = mutableListOf<String>()
        val locationIds = mutableListOf<String>()
        for (it in data) {
            practitionerIds.add(it.callerId)
            practitionerIds.add(it.doctorId)
            locationIds.add(it.locationId)
        }

        val practitioners = if (practitionerIds.isNotEmpty()) {
            createResourceByIdMap(
                    resourceService
                            .byIds(ResourceType.Practitioner, practitionerIds)
            )
        } else {
            mapOf()
        }
        val locations = if (locationIds.isNotEmpty()) {
            createResourceByIdMap(
                    resourceService
                            .byIds(ResourceType.Location, locationIds)
            )
        } else {
            mapOf()
        }

        return PagedResponse(
                page = request.page,
                pagesCount = ceil(totalCount.toDouble() / pageSize).toInt(),
                totalItemsCount = totalCount.toInt(),
                data = data.map { toApi(it, practitioners, locations) }
        )
    }

    private fun toApi(
            entity: ru.viscur.dh.datastorage.impl.entity.DoctorCall,
            practitioners: Map<String, Practitioner>?,
            locations: Map<String, Location>?
    ): DoctorCall {
        return DoctorCall(
                id = entity.id,
                dateTime = entity.dateTime,
                caller =
                if (practitioners == null)
                    resourceService.byId(ResourceType.Practitioner, entity.callerId)
                else
                    practitioners[entity.callerId]
                            ?: error("Not found practitioner by id '${entity.callerId}'"),
                specialization = entity.qualification,
                doctor = if (practitioners == null)
                    resourceService.byId(ResourceType.Practitioner, entity.doctorId)
                else
                    practitioners[entity.doctorId]
                            ?: error("Not found practitioner by id '${entity.doctorId}'"),

                goal = entity.goal,
                patientSeverity = entity.patientSeverity,
                location = if (locations == null)
                    resourceService.byId(ResourceType.Location, entity.locationId)
                else
                    locations[entity.locationId]
                            ?: error("Not found location by id '${entity.locationId}'"),
                comment = entity.comment,
                status = entity.status,
                timeToArrival = entity.timeToArrival
        )
    }


    private fun <T> createResourceByIdMap(resources: List<T>): Map<String, T> where T : BaseResource {
        val map = mutableMapOf<String, T>()
        resources.forEach { map[it.id] = it }
        return map
    }

}