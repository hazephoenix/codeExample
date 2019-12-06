package ru.viscur.dh.integration.mis.rest.dto

/**
 * Created at 01.11.2019 11:24 by SherbakovaMA
 *
 * Тело запроса для определения услуг в маршрутном листе и др.
 */
class ServiceRequestPredictBody(
        val diagnosis: String,
        val complaints: List<String>,
        val gender: String
)