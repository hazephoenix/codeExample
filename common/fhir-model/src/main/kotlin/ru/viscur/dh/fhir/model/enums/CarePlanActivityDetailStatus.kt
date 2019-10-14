package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 10:51 by SherbakovaMA
 *
 * Статус назначения [CarePlanActivityDetail]
 */
enum class CarePlanActivityDetailStatus {
    not_started,
    scheduled,
    in_progress,
    on_hold,
    completed,
    cancelled,
    stopped,
    unknown,
    entered_in_error
}