package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.PractitionerCallStorageService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.exception.EntityNotFoundException
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.criteria.DoctorCallCriteriaQueryBuilder
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.practitioner.call.model.AwaitingPractitionerCallRef
import ru.viscur.dh.practitioner.call.model.PractitionerCall
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.math.ceil

@Service
class PractitionerCallStorageServiceImpl(
        val resourceService: ResourceService,
        val doctorCallCriteriaQueryBuilder: DoctorCallCriteriaQueryBuilder
) : PractitionerCallStorageService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    @Tx
    override fun byId(id: String): PractitionerCall {
        val entity = em.find(
                ru.viscur.dh.datastorage.impl.entity.DoctorCall::class.java,
                id
        ) ?: throw EntityNotFoundException(id)
        return toApi(entity, null, null)
    }

    @Tx
    override fun createCall(call: PractitionerCall): PractitionerCall {
        val entity = ru.viscur.dh.datastorage.impl.entity.DoctorCall(
                id = genId(),
                dateTime = call.dateTime,
                callerId = call.caller.id,
                specializationCategory = call.specializationCategory,
                doctorId = call.practitioner.id,
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
    override fun updateCall(call: PractitionerCall): PractitionerCall {
        val entity = em.find(
                ru.viscur.dh.datastorage.impl.entity.DoctorCall::class.java,
                call.id
        ) ?: throw EntityNotFoundException(call.id)
        entity.callerId = call.caller.id
        entity.specializationCategory = call.specializationCategory
        entity.doctorId = call.practitioner.id
        entity.goal = call.goal
        entity.patientSeverity = call.patientSeverity
        entity.locationId = call.location.id
        entity.comment = call.comment
        entity.status = call.status
        entity.timeToArrival = call.timeToArrival
        return toApi(entity, null, null)
    }


    override fun createAwaitingRef(ref: AwaitingPractitionerCallRef) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun updateAwaitingRef(ref: AwaitingPractitionerCallRef) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun removeAwaitingRef(callId: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getAllAwaitingRef(): List<AwaitingPractitionerCallRef> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun findCalls(request: PagedCriteriaRequest<DoctorCallCriteria>): PagedResponse<PractitionerCall> {
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
    ): PractitionerCall {
        return PractitionerCall(
                id = entity.id,
                dateTime = entity.dateTime,
                caller =
                if (practitioners == null)
                    resourceService.byId(ResourceType.Practitioner, entity.callerId)
                else
                    practitioners[entity.callerId]
                            ?: error("Not found practitioner by id '${entity.callerId}'"),
                specializationCategory = entity.specializationCategory,
                practitioner = if (practitioners == null)
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