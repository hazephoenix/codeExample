package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 13.11.2019 12:20 by SherbakovaMA
 *
 * Описание мед. персонала
 *
 * @param practitionerId id
 * @param name ФИО
 */
data class PractitionerDto (
        val practitionerId: String,
        val name: String
)