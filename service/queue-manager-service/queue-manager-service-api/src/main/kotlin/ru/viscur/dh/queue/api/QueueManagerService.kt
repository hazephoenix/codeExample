package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.dto.LocationMonitorDto
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.Severity

/**
 * Сервис управления очередью пациентов
 */
interface QueueManagerService {

    /**
     * Значение настройки Пересчитывать следующий кабинет в очереди
     */
    fun needRecalcNextOffice(): Boolean

    /**
     * Задать значение настройки Пересчитывать следующий кабинет в очереди
     */
    fun recalcNextOffice(value: Boolean)

    /**
     * Пациент получил маршрутный лист: вносим в систему
     * Возвращаем список обследований с заполненными №пп [ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun registerPatient(patientId: String): List<ServiceRequest>

    /**
     * Проставление/перепроставление порядка у невыполненных назначений в маршрутном листе пациента
     */
    fun calcServiceRequestExecOrders(patientId: String, prevOfficeId: String? = null): List<ServiceRequest>

    /**
     * Поставить пациента в очередь
     * У пациента может быть незавершенный маршрутный лист, но его удалили из очереди по какой-либо причине,
     * этой функцией мы снова добавляем его в очередь
     * Если пациент уже в очереди, то ничего не происходит
     */
    fun addToQueue(patientId: String, prevOfficeId: String? = null)

    /**
     * Добавление пациента в очередь в указанный кабинет
     */
    fun addToOfficeQueue(patientId: String, officeId: String)

    /**
     * Вызов пациента на обследование в кабинет (принудительно, в обход очереди, где бы он не стоял)
     */
    fun forceSendPatientToObservation(patientId: String, officeId: String)

    /**
     * Поставить пациента первым в очередь в кабинет
     */
    fun setAsFirst(patientId: String, officeId: String)

    /**
     * Переставить пациента если необходимо:
     * проверяет, актуально ли еще то, что пациент стоит в указанный кабинет - есть ли там назначения
     * Иначе передвигает в след. кабинет
     */
    fun rebasePatientIfNeeded(patientId: String, officeId: String)

    /**
     * Убрать пациента из очереди
     * Перевод пациента в статус [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.READY] - поставить пациента "вне очередей"
     *
     * Используется, например, на тот случай если его очередь настала, но он отошел - его не нужно исключать вообще из системы,
     * но и ждать не имеет смысла.
     * Или если даже он начал обследование, но выяснялось, что по каким-то причинам сейчас осмотр нельзя проводить.
     * Кабинет переводим в статус BUSY, если очередь пациента настала или шло обследование
     */
    fun deleteFromQueue(patientId: String)

    /**
     * Пациент зашел в кабинет
     */
    fun patientEntered(patientId: String, officeId: String): List<ServiceRequest>

    /**
     * Пациент выходит из кабинета (все обследования в этом кабинете, которые есть в маршрутном листе проведены)
     */
    fun patientLeft(patientId: String, officeId: String)

    /**
     * Аналог [patientLeft]
     */
    fun patientLeftByPatientId(patientId: String)

    /**
     * Отменить "вход" пациента в кабинет
     * Если статус пациента [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.GOING_TO_OBSERVATION] или [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.ON_OBSERVATION]
     * Пациент отправляется обратно первым в очередь
     * Кабинет принимает статус "занят" (если пациентов на приеме в кабинет не осталось)
     */
    fun cancelEntering(patientId: String)

    /**
     * Функционал, который нужно сделать после изменения степени тяжести
     */
    fun severityUpdated(patientId: String, severity: Severity)

    /**
     * Кабинет готов принять пациента: смена статуса с CLOSED, BUSY на READY
     * Если кабинет находится в статусе назначенного пациента, ничего не делаем
     */
    fun officeIsReady(officeId: String)

    /**
     * Пригласить след. пациента из очереди в кабинет
     */
    fun enterNextPatient(officeId: String)

    /**
     * Смена статуса кабинета с "готов принять" или с "закрыт" на занят
     */
    fun officeIsBusy(officeId: String)

    /**
     * Смена статуса кабинета с "занят" или "готов принять" на "закрыт"
     * Расформировываем очередь
     */
    fun officeIsClosed(officeId: String)

    /**
     * Удалить устаревшие [ru.viscur.dh.fhir.model.type.LocationExtensionNextOfficeForPatientInfo]
     */
    fun deleteOldNextOfficeForPatientsInfo()

    /**
     * Удалить всю очередь: из базы и из системы
     * и пациентов, и маршрутные листы
     */
    fun deleteQueue()

    /**
     * Удаление истории работы кабинетов и статусов пациентов
     */
    fun deleteHistory()

    /**
     * Очередь в опр. кабинет
     */
    fun queueOfOffice(officeId: String): Bundle

    /**
     * Все [QueueItem] - все элементы очередей для всех кабинетов
     */
    fun queueItems(): List<QueueItem>

    /**
     * Информация для монитора для отображения очереди/приема в кабинет/зоне
     */
    fun locationMonitor(officeId: String): LocationMonitorDto

    /**
     * Отобразить в логах очередь и провалидировать
     * todo только на время отладки
     */
    fun loqAndValidate(): String
}