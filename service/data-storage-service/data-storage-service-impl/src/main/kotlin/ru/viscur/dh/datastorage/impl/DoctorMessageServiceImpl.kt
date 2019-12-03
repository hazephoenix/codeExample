package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.DoctorMessageService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.criteria.DoctorMessageCriteria
import ru.viscur.dh.datastorage.api.exception.EntityNotFoundException
import ru.viscur.dh.datastorage.api.model.message.DoctorMessage as ApiMessage
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.api.response.PagedResponse
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.criteria.DoctorMessageCriteriaQueryBuilder
import ru.viscur.dh.datastorage.impl.entity.DoctorMessage
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import kotlin.math.ceil


@Service
class DoctorMessageServiceImpl(
        val resourceService: ResourceService,
        val queryBuilder: DoctorMessageCriteriaQueryBuilder
) : DoctorMessageService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    @Tx(readOnly = true)
    override fun byId(id: String): ApiMessage {
        val entity = em.find(DoctorMessage::class.java, id)
                ?: throw EntityNotFoundException(id)
        return mapToApiMessage(entity)
    }

    @Tx
    override fun createMessage(message: ApiMessage): ApiMessage {
        val entity = DoctorMessage(
                genId(),
                message.dateTime,
                message.doctor.id,
                message.clinicalImpression.id,
                message.messageType,
                message.hidden
        )
        em.persist(entity)
        return mapToApiMessage(entity)
    }

    @Tx
    override fun updateMessage(message: ApiMessage): ApiMessage {
        val entity = em.find(DoctorMessage::class.java, message.id)
                ?: throw EntityNotFoundException(message.id)
        entity.hidden = message.hidden
        return mapToApiMessage(entity)
    }


    @Tx(readOnly = true)
    override fun findMessages(request: PagedCriteriaRequest<DoctorMessageCriteria>): PagedResponse<ApiMessage> {
        val requestCriteria = request.criteria
        val countCriteria = queryBuilder
                .buildTotalCountQuery(requestCriteria)
        val dataCriteria = queryBuilder
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

        return PagedResponse(
                request.page,
                ceil(totalCount.toDouble() / pageSize).toInt(),
                totalCount.toInt(),
                mapToApiMessages(data)
        )
    }


    private fun mapToApiMessage(source: DoctorMessage): ApiMessage {
        return mapToApiMessages(listOf(source))
                .first()
    }

    private fun mapToApiMessages(source: List<DoctorMessage>): List<ApiMessage> {
        val practitionerIds = mutableListOf<String>()
        val clinicalImpressionIds = mutableListOf<String>()

        for (it in source) {
            practitionerIds.add(it.doctorId)
            clinicalImpressionIds.add(it.clinicalImpressionId)
        }

        val practitionersByIds = resourceService
                .byIds(ResourceType.Practitioner, practitionerIds)
                .groupBy { it.id }
        val clinicalImpressionsByIds = resourceService
                .byIds(ResourceType.ClinicalImpression, clinicalImpressionIds)
                .groupBy { it.id }

        return source.map {
            ApiMessage(
                    it.id,
                    it.dateTime,
                    practitionersByIds[it.doctorId]
                            ?.firstOrNull()
                            ?: throw EntityNotFoundException(it.doctorId),
                    clinicalImpressionsByIds[it.clinicalImpressionId]
                            ?.firstOrNull()
                            ?: throw EntityNotFoundException(it.clinicalImpressionId),
                    it.messageType,
                    it.hidden
            )
        }
    }

}