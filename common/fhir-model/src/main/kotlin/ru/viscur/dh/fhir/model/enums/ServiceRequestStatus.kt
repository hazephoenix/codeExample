package ru.viscur.dh.fhir.model.enums

/**
 * Created at 07.10.2019 14:00 by SherbakovaMA
 *
 * Статусы направления на [обследование ServiceRequest] [ru.viscur.dh.fhir.model.entity.ServiceRequest]
 */
enum class ServiceRequestStatus {
    /**
     * Назначено
     */
    active,
    /**
     * Проведено, ожидает результата (есть привязанный [ru.viscur.dh.fhir.model.entity.Observation], ожидающий результата)
     */
    waiting_result,
    /**
     * Результат готов (есть привязанный [ru.viscur.dh.fhir.model.entity.Observation], с готовым результатом)
     */
    completed,
    /**
     * Отменен
     */
    cancelled
}