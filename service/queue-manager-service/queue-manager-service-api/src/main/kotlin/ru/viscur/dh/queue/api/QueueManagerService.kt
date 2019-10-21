package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.queue.api.cmd.RegisterUserCMD
import ru.viscur.dh.queue.api.model.Office
import ru.viscur.dh.queue.api.model.RouteSheet
import ru.viscur.dh.queue.api.model.SurveyType
import ru.viscur.dh.queue.api.model.User

/**
 * Сервис управления очередью пациентов
 */
interface QueueManagerService {

    /**
     * Пациент получил маршрутный лист: вносим в систему
     * Возвращаем список обследований с заполненными №пп [ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun registerPatient(patientId: String): List<ServiceRequest>

    /**
     * Поставить пациента в очередь
     * У пациента может быть незавершенный маршрутный лист, но его удалили из очереди по какой-либо причине,
     * этой функцией мы снова добавляем его в очередь
     * Если пациент уже в очереди, то ничего не происходит
     *
     * TODO moveUserToNextQueue?
     */
    fun addToOfficeQueue(patientId: String)

    /**
     * Убрать пациента из очереди
     * Перевод пациента в статус [ru.viscur.dh.queue.api.model.UserInQueueStatus.READY] - поставить пациента "вне очередей"
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
     * Кабинет готов принять пациента: смена статуса с [OfficeStatus.CLOSED], [OfficeStatus.BUSY] на [OfficeStatus.READY]
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
     * Отобразить в логах очередь и провалидировать
     * todo только на время отладки
     */
    fun loqAndValidate(): String
}