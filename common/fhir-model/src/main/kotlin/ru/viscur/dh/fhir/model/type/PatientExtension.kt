package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 03.10.2019 8:29 by SherbakovaMA
 *
 * Доп. поля пациента
 *
 * @param nationality национальность
 * @param birthPlace место рождения
// * @param citizenship гражданство
 */
class PatientExtension @JsonCreator constructor(
        @JsonProperty("nationality") val nationality: String,
        @JsonProperty("birthPlace") val birthPlace: Address
//        ,
//        @JsonProperty("citizenship") val citizenship: String todo пока не нужно?
)