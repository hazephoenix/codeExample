package ru.viscur.dh.datastorage.impl.criteria

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.criteria.DoctorCallCriteria
import ru.viscur.dh.datastorage.api.request.PagedCriteriaRequest
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.entity.DoctorCall
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.criteria.CriteriaQuery
import javax.persistence.criteria.Predicate
import javax.persistence.criteria.Root

@Component
class DoctorCallCriteriaQueryBuilder {
    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    /**
     * Построение запроса (JPA Criteria API), на выборку данных
     */
    fun buildQuery(criteria: DoctorCallCriteria): CriteriaQuery<DoctorCall> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(DoctorCall::class.java)
        val root = query.from(DoctorCall::class.java)

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
    fun buildTotalCountQuery(criteria: DoctorCallCriteria): CriteriaQuery<Long> {
        val cb = em.criteriaBuilder
        val query = cb.createQuery(Long::class.java)
        val root = query.from(DoctorCall::class.java)

        val whereCauses = createWhereCauses(criteria, root)
        query
                .select(cb.count(root))
                .where(*whereCauses.toTypedArray())
        return query
    }

    private fun createWhereCauses(criteria: DoctorCallCriteria, root: Root<DoctorCall>): MutableList<Predicate> {
        val whereCauses = mutableListOf<Predicate>()

        val callerIdIn = criteria.callerIdIn
        if (!callerIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("callerId").`in`(callerIdIn))
        }
        val doctorIdIn = criteria.doctorIdIn
        if (!doctorIdIn.isNullOrEmpty()) {
            whereCauses.add(root.get<String>("doctorId").`in`(doctorIdIn))
        }
        return whereCauses
    }

}