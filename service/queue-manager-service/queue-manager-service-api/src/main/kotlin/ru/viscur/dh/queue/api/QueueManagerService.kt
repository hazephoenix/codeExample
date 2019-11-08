package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.entity.ServiceRequest

/**
 * Сервис управления очередью пациентов
 */
interface QueueManagerService {

    /**
     * Значение настройки пересчитывать следующий кабинет в очереди
     */
    fun needRecalcNextOffice(): Boolean

    /**
     * Задать значение настройки пересчитывать следующий кабинет в очереди
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
     * Вызов пациента на обследование в кабинет (принудительно, в обход очереди, где бы он не стоял)
     * Статус кабинета д б занят/свободен/закрыт
     */
    fun forceSendPatientToObservation(patientId: String, officeId: String)

    /**
     * Убрать пациента из очереди
     * Перевод пациента в статус [ru.viscur.dh.fhir.model.enums.PatientQueueStatus.READY] - поставить пациента "вне очередей"
     *
     * Используется, например, на тот случай если его очередь настала, но он отошел - его не нужно исключать вообще из системы,
     * но и ждать не имеет смысла.
     * Или если даже он начал обследование, но выяснялось, что по каким-то причинам сейчас осмотр нельзя проводить.
     * Кабинет переводим в статус BUSY, если очередь пациента настала или шло обследование
     */
    fun deleteFromOfficeQueue(patientId: String)

    /**
     * Пациент зашел в кабинет
     */
    fun patientEntered(patientId: String, officeId: String): List<ServiceRequest>

    /**
     * Пациент выходит из кабинета (все обследования в этом кабинете, которые есть в маршрутном листе проведены)
     */
    fun patientLeft(officeId: String)

    /**
     * Аналог [patientLeft]
     */
    fun patientLeftByPatientId(patientId: String)

    /**
     * Отменить "вход" пациента в кабинет
     * Если статус кабинета [ru.viscur.dh.fhir.model.enums.LocationStatus.WAITING_PATIENT] или [ru.viscur.dh.fhir.model.enums.LocationStatus.OBSERVATION]
     * Пациент отправляется обратно первым в очередь
     * Кабинет принимает статус "занят"
     */
    fun cancelEntering(officeId: String)

    /**
     * Кабинет готов принять пациента: смена статуса с CLOSED, BUSY на READY
     * Если кабинет находится в статусе назначенного пациента, ничего не делаем
     */
    fun officeIsReady(officeId: String)

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
     * Отобразить в логах очередь и провалидировать
     * todo только на время отладки
     */
    fun loqAndValidate(): String
}