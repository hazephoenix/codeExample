package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.enums.ObservationStatus

/**
 * Created at 04.11.2019 16:04 by SherbakovaMA
 *
 * Информация о обследовании
 */
data class ObservationInfo(
        val basedOnId: String,
        val code: String,
        val status: ObservationStatus = ObservationStatus.registered,
        val valueInt: Int? = null,
        val valueStr: String? = null,
        val id: String? = null
)