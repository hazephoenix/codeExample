package ru.viscur.dh.datastorage.impl.criteria

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.criteria.DoctorMessageCriteria
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.entity.DoctorMessage
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Component
class DoctorMessageCriteriaQueryBuilder {
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    fun buildQuery(criteria: DoctorMessageCriteria): CriteriaQuery<DoctorMessage> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(DoctorMessage::class.java)
        val root = query.from(DoctorMessage::class.java)

        val whereCauses = createWhereCauses(criteria, root)
        query
                .select(root)
                .where(*whereCauses.toTypedArray())
        if (criteria.orderBy != null) {
            query.orderBy(criteria.orderBy?.map {
                if (it.desc) cb.desc(root.get<Any>(it.attributeName))
                else cb.asc(root.get<Any>(it.attributeName))
            })
        }
        return query
    }

    fun buildTotalCountQuery(criteria: DoctorMessageCriteria): CriteriaQuery<Long> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val root = query.from(DoctorMessage::class.java)

        val whereCauses = createWhereCauses(criteria, root)
        query
                .select(cb.count(root))
                .where(*whereCauses.toTypedArray())
        return query
    }

    private fun createWhereCauses(criteria: DoctorMessageCriteria, root: Root<DoctorMessage>): List<Predicate> {
        val cb = em.criteriaBuilder
        val whereCauses = mutableListOf<Predicate>()
        when (criteria.type) {
            DoctorMessageCriteria.Type.Actual -> false
            DoctorMessageCriteria.Type.Hidden -> true
            DoctorMessageCriteria.Type.All -> null
        }?.let {
            whereCauses.add(cb.equal(root.get<Boolean>("hidden"), it))
        }
        val doctorIdIn = criteria.doctorIdIn;
        if (!doctorIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("doctorId").`in`(doctorIdIn))
        }
        return whereCauses
    }
}


