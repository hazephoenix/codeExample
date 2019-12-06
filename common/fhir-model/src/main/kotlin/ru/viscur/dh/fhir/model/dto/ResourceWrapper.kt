package ru.viscur.dh.fhir.model.dto

import java.math.BigInteger
import java.util.*

/**
 * Created at 30.09.2019 14:08 by SherbakovaMA
 *
 * Ресурс (любая сущность fhir)
 * @param id id ресурса (строковый)
 * @param txid id ресурса (числовой)
 * @param resourceType тип ресурса
 * @param status статус: 'created', 'updated', 'deleted', 'recreated'
 * @param resource данные
 */
data class ResourceWrapper(
        val id: String,
        val txid: BigInteger,
        val ts: Date,
        val resourceType: String,
        val status: String,
        val resource: Any
)