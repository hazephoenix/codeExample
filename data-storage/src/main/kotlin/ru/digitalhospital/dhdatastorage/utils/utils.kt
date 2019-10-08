package ru.digitalhospital.dhdatastorage.utils

import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*

/**
 * Текущее время в формате Date
 */
fun now() = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant())

/**
 * Дата в формате строки yyyy.MM.dd HH:mm:ss
 */
fun Date?.toStringFmtWithSeconds(): String? {
    this ?: return null
    val pattern = "yyyy.MM.dd HH:mm:ss"
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(this)
}

/**
 * Дата в формате строки yyyy.MM.dd HH:mm
 */
fun Date?.toStringFmt(): String? {
    this ?: return null
    val pattern = "yyyy.MM.dd HH:mm"
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(this)
}