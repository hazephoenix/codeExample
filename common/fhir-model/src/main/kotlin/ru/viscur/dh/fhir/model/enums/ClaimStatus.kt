package ru.viscur.dh.fhir.model.enums

/**
 * Created at 03.10.2019 17:16 by SherbakovaMA
 *
 * Статус обращения пациента [ru.viscur.dh.fhir.model.entity.Claim]
 */
enum class ClaimStatus {
    active,
    cancelled,
    draft,
    entered_in_error
}