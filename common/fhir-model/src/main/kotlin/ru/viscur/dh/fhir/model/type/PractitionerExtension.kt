package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 23.11.2019 12:12 by SherbakovaMA
 *
 * Доп. поля для мед. работника [ru.viscur.dh.fhir.model.entity.Practitioner]
 *
 * @param blocked заблокирован
 * @param onWork на работе (на смене)
 * @param onWorkInOfficeId в каком кабинете на работе (на смене). заполняется для врачей-"диагностов" - кто работает в определенном кабинете. не для врачей по осмотрам
 * @param qualificationCategory код категории специальности (корневое значение из справочника специальности врача)
 */
class PractitionerExtension @JsonCreator constructor(
        @JsonProperty("blocked") var blocked: Boolean = false,
        @JsonProperty("onWork") var onWork: Boolean = false,
        @JsonProperty("onWorkInOfficeId") var onWorkInOfficeId: String? = null,
        @JsonProperty("qualificationCategory") var qualificationCategory: String
)