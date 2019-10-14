package ru.viscur.dh.fhir.model.dto

/**
 * Created at 30.09.2019 16:18 by SherbakovaMA
 *
 * Данные для запросов по ресурсам
 * @param resource данные ресурса
 * @param resourceType тип ресурса
 * @param id see [ResourceWrapper.id]
 * @param txid see [ResourceWrapper.txid]
 */
class RequestBodyForResource(
        val resource: String?,
        val resourceType: String? = null,
        val id: String? = null,
        val txid: Long? = null
)
