package ru.viscur.dh.datastorage.impl.criteria

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.criteria.PractitionerCallCriteria
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.entity.PractitionerCallEntity
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Component
class PractitionerCallCriteriaQueryBuilder {
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    /**
     * Построение запроса (JPA Criteria API), на выборку данных
     */
    fun buildQuery(criteria: PractitionerCallCriteria): CriteriaQuery<PractitionerCallEntity> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(PractitionerCallEntity::class.java)
        val root = query.from(PractitionerCallEntity::class.java)

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

    /**
     * Построение запроса (JPA Criteria API), на выборку количество строк которые попадают
     * под ограничение выборки
     */
    fun buildTotalCountQuery(criteria: PractitionerCallCriteria): CriteriaQuery<Long> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val root = query.from(PractitionerCallEntity::class.java)

        val whereCauses = createWhereCauses(criteria, root)
        query
                .select(cb.count(root))
                .where(*whereCauses.toTypedArray())
        return query
    }

    private fun createWhereCauses(criteria: PractitionerCallCriteria, root: Root<PractitionerCallEntity>): MutableList<Predicate> {
        val whereCauses = mutableListOf<Predicate>()

        val callerIdIn = criteria.callerIdIn
        if (!callerIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("callerId").`in`(callerIdIn))
        }
        val doctorIdIn = criteria.practitionerIdIn
        if (!doctorIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("practitionerId").`in`(doctorIdIn))
        }
        return whereCauses
    }

}