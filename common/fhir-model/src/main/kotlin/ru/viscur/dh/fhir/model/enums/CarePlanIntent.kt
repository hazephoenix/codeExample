package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 10:30 by SherbakovaMA
 *
 * Цель [плана назначений пациенту CarePlan][ru.viscur.dh.fhir.model.entity.CarePlan]
 */
enum class CarePlanIntent {
    proposal,
    plan,
    order,
    option
}