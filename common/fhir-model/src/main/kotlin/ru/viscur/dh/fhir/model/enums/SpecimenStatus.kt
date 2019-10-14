package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 9:40 by SherbakovaMA
 *
 * Статус пробы анализа [ru.viscur.dh.fhir.model.entity.Specimen]
 */
enum class SpecimenStatus {
    available,
    unavailable,
    unsatisfactory,
    entered_in_error
}