package ru.viscur.dh.fhir.model.utils

import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.type.ServiceRequestExtension
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.Period
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

/**
 * Количество миллисекунд в секунде
 */
const val MILLISECONDS_IN_SECOND = 1000

/**
 * Количество секунд в минуте
 */
const val SECONDS_IN_MINUTE = 60

/**
 * Количество минут в часе
 */
const val MINUTES_IN_HOUR = 60

/**
 * Количество часов в сутках
 */
const val HOURS_IN_DAY = 24

/**
 * Перевод миллисекунд в секунды
 */
fun msToSeconds(ms: Long) = (ms / MILLISECONDS_IN_SECOND).toInt()

/**
 * Продолжительность периода от start до end в секундах
 */
fun durationInSeconds(start: Date, end: Date) = msToSeconds(end.time - start.time)

/**
 * Текущее время в формате Date
 */
fun now() = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant())

/**
 * Текущее время в формате TimeStamp
 */
fun nowAsTimeStamp() = now().toTimestamp()

/**
 * Преобразование Date в Timestamp
 */
fun Date.toTimestamp(): Timestamp = Timestamp.from(this.toInstant())

/**
 * Преобразование Timestamp в Date
 */
fun Timestamp.toDate(): Date = Date.from(this.toInstant())

/**
 * Прибавить кол-во дней
 */
fun Date.plusDays(days: Int) = Date(this.time + days * HOURS_IN_DAY * MINUTES_IN_HOUR * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND)

/**
 * Прибавить кол-во минут
 */
fun Date.plusMinutes(minutes: Int) = Date(this.time + minutes * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND)

/**
 * Прибавить кол-во секунд
 */
fun Date.plusSeconds(seconds: Int) = Date(this.time + seconds * MILLISECONDS_IN_SECOND)

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

/**
 * Ссылка на [ru.viscur.dh.fhir.model.entity.Practitioner]
 */
fun referenceToPractitioner(id: String) = Reference(resourceType = ResourceType.Practitioner.id, id = id)

/**
 * Пользуемся [CodeableConcept] однозначно: всегда в coding одно значение
 */
fun CodeableConcept.code(): String = this.coding.first().code

/**
 * Найти [ValueSetName] по его id
 */
fun valueSetNameById(id: String) = ValueSetName.values().find { it.id == id }
        ?: throw Exception("Error. Can't find ValueSet with id '$id'. Available ids: ${ValueSetName.values().map { it.id }.joinToString()}")

fun <T> Bundle.resources(type: ResourceType<T>): List<T> where T : BaseResource =
        this.entry.map { it.resource }.filter { it.resourceType == type.id }.map { it as T }

/**
 * Продолжительность выполнения услуги
 * Если одно из execStart, execEnd не задано, то возвращает null
 */
fun ServiceRequestExtension.execDuration(): Int? = if (execEnd != null && execStart != null) durationInSeconds(execStart!!, execEnd!!) else null

/**
 * Назначение является осмотром ответственного - если указан исполнитель
 */
fun ServiceRequest.isInspectionOfResp() = !this.performer.isNullOrEmpty()

/**
 * Критичное время для удаления [ru.viscur.dh.fhir.model.type.LocationExtensionNextOfficeForPatientInfo]
 * Все записи старее этого времени должны быть удалены
 */
fun criticalTimeForDeletingNextOfficeForPatientsInfo(): Date {
    //сколько минут отображается информация о последующем кабинете для пациентов
    val minutesForShowingNextOfficeForPatients = 3
    return now().plusMinutes(-minutesForShowingNextOfficeForPatients)
}

/**
 * Критичное время для того, чтобы отложить прием пациента, который долго находится в статусе [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.GOING_TO_OBSERVATION]
 */
fun criticalTimeForDelayGoingToObservation(): Date {
    //сколько секунд ожидаем пациента со статусом [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.GOING_TO_OBSERVATION]
    val secondsToWaitGoingToObservationPatient = 30
    return now().plusSeconds(-secondsToWaitGoingToObservationPatient)
}