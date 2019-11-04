package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus

/**
 * Created at 04.11.2019 15:29 by SherbakovaMA
 *
 * Информация о назначении
 */
data class ServiceRequestInfo(
    val code: String,
    val locationId: String,
    val status: ServiceRequestStatus = ServiceRequestStatus.active
)