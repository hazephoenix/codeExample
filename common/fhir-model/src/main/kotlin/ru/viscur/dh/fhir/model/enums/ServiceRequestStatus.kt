package ru.viscur.dh.fhir.model.enums

/**
 * Created at 07.10.2019 14:00 by SherbakovaMA
 *
 * Статусы направления на [обследование ServiceRequest] [ru.viscur.dh.fhir.model.entity.ServiceRequest]
 */
enum class ServiceRequestStatus {
    draft,
    active,
    suspended,
    completed,
    entered_in_error,
    cancelled
}