package ru.viscur.dh.datastorage.impl.criteria

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.criteria.PractitionerMessageCriteria
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.entity.PractitionerMessageEntity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Component
class PractitionerMessageCriteriaQueryBuilder {
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    fun buildQuery(criteria: PractitionerMessageCriteria): CriteriaQuery<PractitionerMessageEntity> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(PractitionerMessageEntity::class.java)
        val root = query.from(PractitionerMessageEntity::class.java)

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

    fun buildTotalCountQuery(criteria: PractitionerMessageCriteria): CriteriaQuery<Long> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val root = query.from(PractitionerMessageEntity::class.java)

        val whereCauses = createWhereCauses(criteria, root)
        query
                .select(cb.count(root))
                .where(*whereCauses.toTypedArray())
        return query
    }

    private fun createWhereCauses(criteria: PractitionerMessageCriteria, root: Root<PractitionerMessageEntity>): List<Predicate> {
        val cb = em.criteriaBuilder
        val whereCauses = mutableListOf<Predicate>()
        when (criteria.type) {
            PractitionerMessageCriteria.Type.Actual -> false
            PractitionerMessageCriteria.Type.Hidden -> true
            PractitionerMessageCriteria.Type.All -> null
        }?.let {
            whereCauses.add(cb.equal(root.get<Boolean>("hidden"), it))
        }
        val doctorIdIn = criteria.doctorIdIn;
        if (!doctorIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("practitionerId").`in`(doctorIdIn))
        }
        return whereCauses
    }
}


