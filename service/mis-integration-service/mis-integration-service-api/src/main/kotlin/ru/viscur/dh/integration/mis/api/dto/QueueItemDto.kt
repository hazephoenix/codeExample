package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 13.11.2019 12:16 by SherbakovaMA
 *
 * Описание элемента/пациента в очереди
 *
 * @param onum № п/п (с 1)
 * @param severity код степень тяжести
 * @param severityDisplay отображаемая степень тяжести
 * @param name ФИО пациента
 * @param age возраст пациента
 * @param estDuration ориентировочное время оказания медицинских услуг
 * @param queueCode код в очереди (З-122...)
 * @param patientId id пациента
 */
data class QueueItemDto(
        val onum: Int,
        val severity: String,
        val severityDisplay: String,
        val name: String,
        val age: Int,
        val estDuration: Int,
        val queueCode: String,
        val patientId: String
)