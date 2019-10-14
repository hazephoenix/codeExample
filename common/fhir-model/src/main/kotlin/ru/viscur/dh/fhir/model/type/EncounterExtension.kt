package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 02.10.2019 18:58 by SherbakovaMA
 *
 * Доп. поля взаимодействия [ru.viscur.dh.fhir.model.entity.Encounter]
 *
 * @param modeOfArrival (тип доставки - пришел сам, скорая, вертолет и т.д., все есть в самом стандарте)
 */
class EncounterExtension @JsonCreator constructor(
        @JsonProperty("modeOfArrival") val modeOfArrival: Coding
)