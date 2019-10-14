package ru.digitalhospital.dhdatastorage.dto

import java.math.BigInteger
import java.sql.Timestamp

/**
 * Created at 30.09.2019 14:08 by SherbakovaMA
 *
 * Ресурс (любая сущность fhir)
 * @param id id ресурса (строковый). Кроме чисел и букв допустимы символы "()_-.,". Недопустимы: пробел, "&", "?", "'", """, "`"
 * @param txid id ресурса (числовой)
 * @param ts время создания записи
 * @param resourceType тип ресурса
 * @param status статус: 'created', 'updated', 'deleted', 'recreated'
 * @param resource данные
 */
data class Resource<T>(
        val id: String,
        val txid: BigInteger,
        val ts: Timestamp,
        val resourceType: String,
        val status: String,
        val resource: T
)