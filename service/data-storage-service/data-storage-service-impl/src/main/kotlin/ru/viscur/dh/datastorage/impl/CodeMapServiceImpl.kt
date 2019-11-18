package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.CodeMapService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.CodeMap
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import javax.persistence.*

/**
 * Created at 23.10.2019 16:07 by SherbakovaMA
 */
@Service
class CodeMapServiceImpl(
        private val resourceService: ResourceService
) : CodeMapService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun codeMap(sourceValueSet: ValueSetName, targetValueSet: ValueSetName, sourceCode: String): CodeMap =
            resourceService.single(ResourceType.CodeMap, RequestBodyForResources(filter = mapOf(
                    "sourceUrl" to "ValueSet/${sourceValueSet.id}",
                    "targetUrl" to "ValueSet/${targetValueSet.id}",
                    "sourceCode" to sourceCode
            )))

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

    override fun icdByAnyComplaints(complaints: List<String>, take: Int): List<String> {
        // вместо оператора ?| для поиска вхождений используем свой оператор #-#, чтобы избежать
        // конфликтов с оператором ? для упорядоченных параметров Hibernate
        val query = em.createNativeQuery("""
            select cm.resource ->> 'sourceCode' from codemap cm, jsonb_array_elements(cm.resource -> 'targetCode') x
                where x -> 'code' #-# cast(:codes as text[])
                and cm.resource ->> 'targetUrl' = :targetUrl
                limit :take
        """)
        query.setParameter("codes", "{\"${complaints.joinToString("\", \"")}\"}")
        query.setParameter("targetUrl", "ValueSet/Complaints")
        query.setParameter("take", take)
        return query.resultList as List<String>
    }
}
