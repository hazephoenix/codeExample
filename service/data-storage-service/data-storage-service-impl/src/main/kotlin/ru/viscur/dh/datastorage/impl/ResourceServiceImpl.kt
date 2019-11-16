package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext
import javax.persistence.Query

@Service
class ResourceServiceImpl : ResourceService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    @Tx(readOnly = true)
    override fun <T> byId(resourceType: ResourceType<T>, id: String): T
            where T : BaseResource {
        return em.createNativeQuery("select resource_read(?1, ?2)")
                .setParameter(1, resourceType.id.toString())
                .setParameter(2, id)
                .singleResult.toResourceEntity()
                ?: throw Exception("Not found ${resourceType.id} with id = '$id'")
    }

    override fun <T : BaseResource> byIdentifier(resourceType: ResourceType<T>, type: IdentifierType, value: String): T? {
        val query = em
                .createNativeQuery(
                        """
            select resource
            from (
                select identifiers.i ->> 'value' val, jsonb_array_elements(identifiers.i -> 'type' -> 'coding') ->> 'code' identType, identifiers.resource
                  from (select jsonb_array_elements(r.resource -> 'identifier') i, r.resource resource from patient r) identifiers
                ) typeAndValues
            where identType = :resourceType
              and val = :value
                """.trimIndent())
        query.setParameter("resourceType", type.toString())
        query.setParameter("value", value)
        return query.fetchResourceList<T>().firstOrNull()
    }

    @Tx(/*readOnly = true*/)
    override fun <T> all(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): List<T>
            where T : BaseResource {
        val parts = GeneratedQueryParts(requestBody.filter, requestBody.filterLike, requestBody.orderBy)
        val query = em
                .createNativeQuery(
                        """
                    select r.resource 
                    from ${resourceType.id} r
                    ${parts.where()}
                    ${parts.orderBy()}
                """.trimIndent()
                )
                .apply { parts.setParametersTo(this) }
        return query.fetchResourceList()
    }

    @Tx
    override fun <T> create(resource: T): T
            where T : BaseResource {
        return em
                .createNativeQuery("select resource_create(${jsonbParam(1)})")
                .setParameter(1, resource.toJsonb())
                .singleResult
                .toResourceEntity()!!
    }

    @Tx
    override fun <T> update(resourceType: ResourceType<T>, id: String, block: T.() -> Unit): T
            where T : BaseResource {
        for (i in (1..100)) {
            val resource = byId(resourceType, id)
            val initResource = resource.toJsonb()
            resource.block()
            val updated = updateByVersion(resource)
            if (updated != null) {
                em.createNativeQuery("""insert into ${resourceType.id}_history
                    |(id, txid, status, resource)
                    |values (:id, :txid, 'updated', :resource\:\:jsonb)
                """.trimMargin())
                        .setParameter("id", resource.id)
                        .setParameter("txid", resource.meta.versionId)
                        .setParameter("resource", initResource)
                        .executeUpdate()
                return updated
            }
        }
        throw Exception("Error. can't update resource: 100 attempts failed")
    }

    /**
     * Обновление по версии, обязательно вложенное поле id и [BaseResource.meta]->[ru.viscur.dh.fhir.model.entity.ResourceMeta.versionId]
     * При успешном обновлении возвращает отредактированный ресурс
     * Если нет записи с таким id и txid (версией), то не обновит и вернет null
     */
    @Tx
    private fun <T : BaseResource> updateByVersion(resource: T): T? {
        return em.createNativeQuery("select resource_update_by_txid(${jsonbParam(1)}, ?2)")
                .setParameter(1, resource.toJsonb())
                .setParameter(2, resource.meta.versionId)
                .singleResult
                .toResourceEntity()
    }

    @Tx
    override fun <T> deleteById(resourceType: ResourceType<T>, id: String): T
            where T : BaseResource {
        return em.createNativeQuery("select resource_delete(?1, ?2)")
                .setParameter(1, resourceType.id.toString())
                .setParameter(2, id)
                .singleResult
                .toResourceEntity()
                ?: throw Exception("Not found ${resourceType.id} with id = '$id' for deleting")
    }

    @Tx
    override fun <T> deleteAll(resourceType: ResourceType<T>, requestBody: RequestBodyForResources?): Int
            where T : BaseResource {
        val parts = requestBody?.let { GeneratedQueryParts(requestBody.filter) }
        return em
                .createNativeQuery("delete from ${resourceType.id} r ${parts?.where() ?: ""}")
                .apply { parts?.setParametersTo(this) }
                .executeUpdate()
    }


    private class GeneratedQueryParts(
            filter: Map<String, String?>? = null,
            filterLike: Boolean = false,
            orderBy: List<String>? = null
    ) {
        val params = mutableListOf<Any>()
        val whereStatements = mutableListOf<String>()
        val orderStatements = mutableListOf<String>()

        init {
            addWherePart(filter, filterLike)
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
            query.setParameters(params)
        }

        private fun addWherePart(filter: Map<String, String?>?, filterLike: Boolean) {
            filter?.forEach { (field, value) ->
                value?.run {
                    whereStatements.add("r.resource ->> ?${params.size + 1} ${if (filterLike) "ilike" else "="} ?${params.size + 2}")
                    params.add(field)
                    params.add(if (filterLike) "%$value%" else value)
                } ?: run {
                    whereStatements.add("r.resource ->> ?${params.size + 1} is null")
                    params.add(field)
                }
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