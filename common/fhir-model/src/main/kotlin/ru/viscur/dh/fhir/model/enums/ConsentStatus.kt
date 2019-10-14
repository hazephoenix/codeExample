package ru.viscur.dh.fhir.model.enums

/**
 * Created at 03.10.2019 9:27 by SherbakovaMA
 *
 * Статус соглашения [ru.viscur.dh.fhir.model.entity.Consent]
 */
enum class ConsentStatus {
    draft,
    proposed,
    active,
    rejected,
    inactive,
    entered_in_error
}