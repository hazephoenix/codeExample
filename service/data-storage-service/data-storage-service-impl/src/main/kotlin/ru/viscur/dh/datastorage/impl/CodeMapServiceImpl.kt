package ru.viscur.dh.datastorage.impl

import com.fasterxml.jackson.databind.node.*
import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.CodeMap
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.util.*
import javax.persistence.*
import kotlin.reflect.jvm.internal.impl.load.kotlin.*

/**
 * Created at 23.10.2019 16:07 by SherbakovaMA
 */
@Service
class CodeMapServiceImpl(
        private val resourceService: ResourceService
) : CodeMapService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun codeMapNullable(sourceValueSet: ValueSetName, targetValueSet: ValueSetName, sourceCode: String): CodeMap? {
        val query = em.createNativeQuery("""
            select cm.resource from
                (with recursive r AS (
                    SELECT r1.resource ->> 'code' as code, r1.resource ->> 'parentCode' as parentCode
                    FROM concept r1
                    WHERE r1.resource ->> 'system' = :sourceUrl
                      and r1.resource ->> 'code' = :sourceCode
            
                    UNION
            
                    SELECT r2.resource ->> 'code' as code, r2.resource ->> 'parentCode' as parentCode
                    FROM concept r2
                             JOIN r
                                  ON r2.resource ->> 'code' = r.parentCode
                    where r2.resource ->> 'system' = :sourceUrl
                )
                select code from r) r
            join
                (select cm.resource
                 from codeMap cm
                 where cm.resource ->> 'sourceUrl' = :sourceUrl
                   and cm.resource ->> 'targetUrl' = :targetUrl) cm
            on cm.resource ->> 'sourceCode' = r.code
        """)
        query.setParameter("sourceCode", sourceCode)
        query.setParameter("sourceUrl", "ValueSet/${sourceValueSet.id}")
        query.setParameter("targetUrl", "ValueSet/${targetValueSet.id}")
        return query.fetchResource()
    }

    override fun all(sourceValueSet: ValueSetName, targetValueSet: ValueSetName): List<CodeMap> =
            resourceService.all(ResourceType.CodeMap, RequestBodyForResources(filter = mapOf(
                    "sourceUrl" to "ValueSet/${sourceValueSet.id}",
                    "targetUrl" to "ValueSet/${targetValueSet.id}"
            )))

    override fun icdByAllComplaints(complaints: List<String>, take: Int): List<String> {
        val query = em.createNativeQuery("""
            select cm.resource ->> 'sourceCode' from codemap cm
                where cm.resource -> 'targetCode' @> cast(:targetCodes as jsonb)
                and cm.resource ->> 'targetUrl' = :targetUrl
                limit :take
        """)
        query.setParameter("targetCodes", complaints.map { CodeMapTargetCode(it).toJson() }.toString())
        query.setParameter("targetUrl", "ValueSet/Complaints")
        query.setParameter("take", take)
        return query.resultList as List<String>
    }

    override fun icdByAnyComplaints(complaints: List<String>, take: Int): List<ComplaintOccurrence?> {
        // вместо оператора ?| для поиска вхождений используем свой оператор #-#, чтобы избежать
        // конфликтов с оператором ? для упорядоченных параметров Hibernate
        val query = em.createNativeQuery("""
            --select cm.resource ->> 'sourceCode' from codemap cm, jsonb_array_elements(cm.resource -> 'targetCode') x
                --where x -> 'code' #-# cast(:codes as text[])
                --and cm.resource ->> 'targetUrl' = :targetUrl
                --limit :take
            select cm.resource ->> 'sourceCode', count(x -> 'code') as codeCount
                from codemap cm, jsonb_array_elements(cm.resource -> 'targetCode') as x
                where  x ->> 'code' = ANY(cast(:codes as text[]))
                and cm.resource ->> 'id' in (
                        select cm.resource ->> 'id' from codemap cm
                        where cm.resource ->> 'targetUrl'= :targetUrl
                    )
                group by cm.resource
                order by codeCount desc
                limit :take
        """)
        query.setParameter("codes", "{\"${complaints.joinToString("\", \"")}\"}")
        query.setParameter("targetUrl", "ValueSet/Complaints")
        query.setParameter("take", take)

        return query.resultList.mapNotNull {
            it as Array<Object>
            ComplaintOccurrence(it[0].toString(), it[1].toString().toInt())
        }
    }
}

