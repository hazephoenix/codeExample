package ru.viscur.dh.fhir.model.utils

import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Reference
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
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

fun genId() = UUID.randomUUID().toString()

/**
 * Из даты в LocalDate
 */
fun Date.toLocalDate() = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

/**
 * Возраст, если дата определения это дата рождения
 */
fun Date.toAge() = Period.between(this.toLocalDate(), now().toLocalDate()).years

/**
 * Ссылка на [ru.viscur.dh.fhir.model.entity.Patient]
 */
fun referenceToPatient(id: String) = Reference(resourceType = ResourceType.Patient.id, id = id)

/**
 * Ссылка на [ru.viscur.dh.fhir.model.entity.Location]
 */
fun referenceToLocation(id: String) = Reference(resourceType = ResourceType.Location.id, id = id)