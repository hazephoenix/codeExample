package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 29.10.2019 18:17 by SherbakovaMA
 *
 * Мета-информация о ресурсе (версионность)
 * @param versionId id версии
 */
class ResourceMeta @JsonCreator constructor(
        @JsonProperty("versionId") val versionId: Int? = null
)