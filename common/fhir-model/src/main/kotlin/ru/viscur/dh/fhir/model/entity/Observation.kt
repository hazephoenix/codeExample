package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.ObservationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.type.Annotation
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*

/**
 * Created at 01.10.2019 13:56 by SherbakovaMA
 *
 * Измерение/назначенное обследование
 * [info](http://fhir-ru.github.io/observation.html)
 * Значение хранится в одном из полей value*
 *
 * @param status статус, [ObservationStatus]
 * @param issued дата-время назначения
 * @param code тип обследования/наблюдения
 * @param basedOn по какому направлению сделано (основание). Заполняется если по маршр. листу [CarePlan], ссылка на [ServiceRequest]
 * @param performer кто исполнил (ссылка на [Practitioner], [Organization])
 * @param subject пациент, кому назначили, над кем производили измерение (ссылка на [Patient])
 * @param specimen образец, используемый для анализа (кровь, моча и проч.) (проба для анализа), ссылка на [Specimen]
 * @param note комментарии
 * @param interpretation интерпретация результата (Высокое/низкое/нормальное значение и т.п.)
 * @param valueQuantity результат в формате [Quantity]
 * @param valueCodeableConcept результат в формате [CodeableConcept]
 * @param valueString результат в формате строки
 * @param valueBoolean результат в формате Булево
 * @param valueInteger результат в формате Целое
 * @param valueSampledData результат в формате набора значений измерений
 */
class Observation @JsonCreator constructor(
        @JsonProperty("id") id: String = genId(),
        @JsonProperty("identifier") identifier: List<Identifier>? = null,
        @JsonProperty("resourceType") resourceType: ResourceType.ResourceTypeId = ResourceType.Observation.id,
        @JsonProperty("status") var status: ObservationStatus = ObservationStatus.registered,
        @JsonProperty("issued") val issued: Date,
        @JsonProperty("code") val code: CodeableConcept,
        @JsonProperty("basedOn") var basedOn: Reference? = null,
        @JsonProperty("performer") var performer: List<Reference>,
        @JsonProperty("subject") var subject: Reference,
        @JsonProperty("specimen") val specimen: Reference? = null,
        @JsonProperty("note") val note: List<Annotation>? = null,
        @JsonProperty("interpretation") val interpretation: List<CodeableConcept>? = null,
        @JsonProperty("valueQuantity") var valueQuantity: Quantity? = null,
        @JsonProperty("valueCodeableConcept") var valueCodeableConcept: CodeableConcept? = null,
        @JsonProperty("valueString") var valueString: String? = null,
        @JsonProperty("valueBoolean") var valueBoolean: Boolean? = null,
        @JsonProperty("valueInteger") var valueInteger: Int? = null,
        @JsonProperty("valueSampledData") var valueSampledData: SampledData? = null
        //val valueRange:  Range ,//todo подключить если необходимо использование
        //val valueRatio:  Ratio ,
        //val valueTime: <time>,
        //val valueDateTime: <dateTime>,
        //val valuePeriod:  Period ,
) : BaseResource(id, identifier, resourceType)