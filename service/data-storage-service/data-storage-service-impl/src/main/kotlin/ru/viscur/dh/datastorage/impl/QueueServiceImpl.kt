package ru.viscur.dh.datastorage.impl

import ru.viscur.dh.datastorage.api.QueueService
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.QueueItem
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 16.10.2019 12:16 by SherbakovaMA
 */
class QueueServiceImpl: QueueService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun queueItemsOfOffice(officeId: String): List<QueueItem> {
        val query = em.createNativeQuery("""
            select r.resource
            from queueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef
            order by r.resource ->> 'onum'
            """)
        query.setParameter("officeRef", "Location/$officeId")
        return query.fetchResourceList()
    }

    @Tx
    override fun deleteQueueItemsOfOffice(officeId: String) {
        val query = em.createNativeQuery("""
            delete
            from queueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef""")
        query.setParameter("officeRef", "Location/$officeId")
    }
}