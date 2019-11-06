package ru.viscur.dh.queue.impl

import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.now
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

/**
 * Created at 04.09.2019 8:56 by SherbakovaMA
 *
 * Вспомогательные элементы проекта
 *
 * TODO часть можно унести в common
 */

/**
 * Приоритетные степени тяжести пациента
 */
val SEVERITY_WITH_PRIORITY = listOf(Severity.RED, Severity.YELLOW)

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
 * Из даты в LocalDate
 */
fun Date.toLocalDate() = this.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()

const val AGE_GROUP_YOUNGER_18 = 0
const val AGE_GROUP_BETWEEN_18_AND_50 = 1
const val AGE_GROUP_OLDER_50 = 2
/**
 * Возрастная группа по дате рождения
 */
fun ageGroup(birthDate: Date?): Int {
    birthDate?:throw Exception("birthDate is null")
    val age = Period.between(birthDate.toLocalDate(), now().toLocalDate()).years
    return if (age < 18) AGE_GROUP_YOUNGER_18 else if (age < 50) AGE_GROUP_BETWEEN_18_AND_50 else AGE_GROUP_OLDER_50
}

/**
 * Из строки в дату
 */
fun date(timeStr: String) = SimpleDateFormat("dd.MM.yyyy hh:mm").parse(timeStr)

/**
 * Место приема пациентов фельдшером - фиктивный id
 */
const val RECEPTION = "RECEPTION"
/**
 * id кабинетов
 */
const val OFFICE101 = "Office:101"
const val OFFICE140 = "Office:140"
const val OFFICE151 = "Office:151"
const val OFFICE117 = "Office:117"
const val OFFICE104 = "Office:104"
const val OFFICE139 = "Office:139"
const val OFFICE149 = "Office:149"
const val OFFICE129 = "Office:129"
const val OFFICE130 = "Office:130"
const val OFFICE202 = "Office:202"