package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 10.10.2019 15:17 by SherbakovaMA
 *
 * Элемент списка [ListResource][ru.viscur.dh.fhir.model.entity.ListResource]
 * @param item ссылка на любой ресурс
 */
class ListResourceEntry @JsonCreator constructor(
        @JsonProperty("item") val item: Reference
)