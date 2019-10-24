package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.CodeMapService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.CodeMap
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Created at 23.10.2019 16:07 by SherbakovaMA
 */
@Service
class CodeMapServiceImpl(
        private val resourceService: ResourceService
) : CodeMapService {

    override fun codeMap(sourceValueSetId: String, targetValueSetId: String, sourceCode: String): CodeMap =
            resourceService.single(ResourceType.CodeMap, RequestBodyForResources(filter = mapOf(
                    "sourceUrl" to "ValueSet/$sourceValueSetId",
                    "targetUrl" to "ValueSet/$targetValueSetId",
                    "sourceCode" to sourceCode
            )))
}