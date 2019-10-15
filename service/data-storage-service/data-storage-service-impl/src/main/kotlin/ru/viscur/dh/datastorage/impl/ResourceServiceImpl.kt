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
        // TODO parameter queries
        val wherePart = makeWherePart(requestBody.filter)
        val orderByPart = " order by " + (requestBody.orderBy?.joinToString(", ") {
            val nameAndOrderType = it.split(" ")
            "r.resource->>'${nameAndOrderType[0]}' " + (if (nameAndOrderType.size > 1) nameAndOrderType[1] else "")
        } ?: "txid")
        val items = em.createNativeQuery(
                """
                    select r.id, 
                           r.txid, 
                           r.ts, 
                           r.resource_type, 
                           r.status, 
                           r.resource 
                    from $resourceType r
                    $wherePart
                    $orderByPart
                """.trimIndent()
        ).resultList
        return items.map { item ->
            item as Array<Any>
            Resource(
                    item[0] as String,
                    item[1] as BigInteger,
                    item[2] as Timestamp,
                    item[3] as String,
                    item[4] as String,
                    item[5].toResourceEntity<T>()!!
            )
        }
    }

    @Tx
    override fun <T> create(resource: T): T?
            where T : BaseResource {
        return em
                .createNativeQuery("select fhirbase_create(${jsonbParam(1)})")
                .setParameter(1, resource.toJsonb())
                .singleResult
                .toResourceEntity()
    }

    @Tx
    override fun <T> update(resource: T): T?
            where T : BaseResource {
        return em.createNativeQuery("select fhirbase_update('${resource.toJsonb()}'\\:\\:jsonb)")
//                .setParameter(1, resource.toJsonb())
                .singleResult
                .toResourceEntity()
    }

    @Tx
    override fun <T> deleteById(resourceType: ResourceType<T>, id: String): T?
            where T : BaseResource {
        return em.createNativeQuery("select fhirbase_delete(?1, ?2)")
                .setParameter(1, resourceType.id.toString())
                .setParameter(2, id)
                .singleResult
                .toResourceEntity()

    }

    @Tx
    override fun <T> deleteAll(resourceType: ResourceType<T>, requestBody: RequestBodyForResources): Int
            where T : BaseResource {
        val wherePart = makeWherePart(requestBody.filter)
        return em.createNativeQuery("delete from $resourceType r$wherePart").executeUpdate()
    }


    private fun makeWherePart(params: Map<String, String>): String =
            // TODO use parameters
            if (params.isNotEmpty()) {
                " where " +
                        params.map { (key, value) ->
                            "r.resource ->> '$key' ilike '%$value%'"
                        }.joinToString(" and ")
            } else ""






}