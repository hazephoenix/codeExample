package ru.viscur.dh.queue.impl

import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.queue.api.model.UserType
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
 * Текущее время
 */
fun now() = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant())

/**
 * Количество миллисекунд в секунде
 */
const val MILLISECONDS_IN_SECOND = 1000

/**
 * Количество секунд в минуте
 */
const val SECONDS_IN_MINUTE = 60

/**
 * Перевод миллисекунд в секунды
 */
fun msToSeconds(ms: Long) = (ms / MILLISECONDS_IN_SECOND).toInt()

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