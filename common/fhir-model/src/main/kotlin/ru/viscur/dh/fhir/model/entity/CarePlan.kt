package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.CarePlanIntent
import ru.viscur.dh.fhir.model.enums.CarePlanStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CarePlanActivity
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Created at 04.10.2019 10:26 by SherbakovaMA
 *
 * План назначений пациенту
 * Включает направления на различные обследования, по сути, это маршрутный лист пациента
 * [info](http://fhir-ru.github.io/careplan.html)
 *
 * @param status статус, [CarePlanStatus]
 * @param intent цель, [CarePlanIntent]
 * @param created дата-время создания
 * @param title заголовок
 * @param subject пациент, ссылка на [Patient]
 * @param author ответственный, ссылка на [Practitioner]
 * @param contributor кто составил план, ссылка на [Practitioner]
 * @param activity список назначений, [CarePlanActivity]
 */
class CarePlan @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.CarePlan.id,
        @JsonProperty("status") var status: CarePlanStatus = CarePlanStatus.active,
        @JsonProperty("intent") val intent: CarePlanIntent = CarePlanIntent.plan,
        @JsonProperty("created") val created: Date,
        @JsonProperty("title") val title: String? = null,
        @JsonProperty("subject") val subject: Reference,
        @JsonProperty("author") val author: Reference,
        @JsonProperty("contributor") val contributor: Reference,
        @JsonProperty("activity") var activity: List<CarePlanActivity>
) : BaseResource(id, identifier, resourceType)