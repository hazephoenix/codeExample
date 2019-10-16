package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.digitalhospital.dhdatastorage.dto.Resource
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import java.math.BigInteger
import java.sql.Timestamp
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

@Service
class ResourceServiceImpl : ResourceService {

    @PersistenceContext(name = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    @Tx(readOnly = true)
    override fun <T> byId(resourceType: ResourceType<T>, id: String): T?
            where T : BaseResource {
        return em.createNativeQuery("select fhirbase_read(?1, ?2)")
                .setParameter(1, resourceType.id.toString())
                .setParameter(2, id)
                .singleResult.toResourceEntity()
    }

    @Tx(readOnly = true)
    override fun <T> all(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): List<Resource<T>>
            where T : BaseResource {
        val parts = GeneratedQueryParts(requestBody.filter, requestBody.orderBy)
        val items = em
                .createNativeQuery(
                        """
                    select r.id, 
                           r.txid, 
                           r.ts, 
                           r.resource_type, 
                           r.status, 
                           r.resource 
                    from ${resourceType.id} r
                    ${parts.where()}
                    ${parts.orderBy()}
                """.trimIndent()
                )
                .apply { parts.setParametersTo(this) }
                .resultList
        return items
                .asSequence()
                .map { it as Array<Any> }
                .map {
                    Resource(
                            it[0] as String,
                            it[1] as BigInteger,
                            it[2] as Timestamp,
                            it[3] as String,
                            it[4] as String,
                            it[5].toResourceEntity<T>()!!
                    )
                }
                .toList()
    }

    @Tx
    override fun <T> create(resource: T): T?
            where T : BaseResource {
        return em
                .createNativeQuery("select resource_create(${jsonbParam(1)})")
                .setParameter(1, resource.toJsonb())
                .singleResult
                .toResourceEntity()
    }

    @Tx
    override fun <T> update(resource: T): T?
            where T : BaseResource {
        return em.createNativeQuery("select resource_update(${jsonbParam(1)})")
                .setParameter(1, resource.toJsonb())
                .singleResult
                .toResourceEntity()
    }

    @Tx
    override fun <T> deleteById(resourceType: ResourceType<T>, id: String): T?
            where T : BaseResource {
        return em.createNativeQuery("select resource_delete(?1, ?2)")
                .setParameter(1, resourceType.id.toString())
                .setParameter(2, id)
                .singleResult
                .toResourceEntity()

    }

    @Tx
    override fun <T> deleteAll(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): Int
            where T : BaseResource {
        val parts = GeneratedQueryParts(requestBody.filter)
        return em
                .createNativeQuery("delete from ${resourceType.id} r ${parts.where()}")
                .apply { parts.setParametersTo(this) }
                .executeUpdate()
    }


    private class GeneratedQueryParts(
            filter: Map<String, String>? = null,
            orderBy: List<String>? = null
    ) {
        val params = mutableListOf<Any>()
        val whereStatements = mutableListOf<String>()
        val orderStatements = mutableListOf<String>()

        init {
            addWherePart(filter)
            addOrderPart(orderBy)
        }

        fun where(): String {
            if (whereStatements.isEmpty()) {
                return ""
            }
            return "where ${whereStatements.joinToString(" and ")}"
        }

        fun orderBy(): String {
            if (orderStatements.isEmpty()) {
                return ""
            }
            val stmt = orderStatements.joinToString(", ") {
                "r.resource->> $it"
            }
            return "order by $stmt"
        }

        fun setParametersTo(query: Query) {
            params.forEachIndexed { idx, value ->
                query.setParameter(idx + 1, value)
            }
        }

        private fun addWherePart(filter: Map<String, String>?) {
            filter?.forEach { (field, value) ->
                whereStatements.add("r.resource ->> ?${params.size + 1} ilike ?${params.size + 2}")
                params.add(field)
                params.add("%$value%")
            }
        }

        private fun addOrderPart(orderBy: List<String>?) {
            orderBy?.forEach {
                val parts = it.split(" ")
                if (parts.isNotEmpty()) {
                    var statement = "?${params.size + 1}"
                    if (parts.size > 1) {
                        val order = if ("desc" == parts[1]) "desc" else "asc"
                        statement += " $order"
                    }
                    orderStatements.add(statement)
                    params.add(parts[0])
                }
            }
        }
    }
}