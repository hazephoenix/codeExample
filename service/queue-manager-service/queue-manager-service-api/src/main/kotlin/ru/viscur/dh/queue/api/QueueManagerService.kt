package ru.viscur.dh.queue.api

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
     * Продолжительность обследования не сохраняется в историю, т к оно было прервано.
     * Кабинет переводим в статус READY, запускаем если есть кто-то следующего из очереди.
     */
    fun deleteFromOfficeQueue(patientId: String)

    /**
     * Обследование началось
     *
     * TODO pass only userId and officeId? (or create cmd)
     */
    fun observationStarted(patientId: String, officeId: String)

    /**
     * Обследование закончилось
     *
     * TODO pass only officeId?
     */
    fun observationFinished(officeId: String)

    /**
     * Кабинет готов принять пациента: смена статуса с [OfficeStatus.CLOSED], [OfficeStatus.BUSY] на [OfficeStatus.READY]
     * Если кабинет находится в статусе назначенного пациента, ничего не делаем
     *
     * TODO pass only officeId?
     */
    fun officeIsReady(officeId: String)

    /**
     * Смена статуса кабинета с "готов принять" или с "закрыт" на занят
     *
     * TODO pass only officeId?
     */
    fun officeIsBusy(officeId: String)

    /**
     * Смена статуса кабинета с "занят" или "готов принять" на "закрыт"
     * Расформировываем очередь
     *
     * TODO pass only officeId?
     */
    fun officeIsClosed(officeId: String)

    /**
     * Пациент покинул очередь
     *
     * TODO pass only userId?
     */
    fun patientLeftQueue(patientId: String)

    /**
     * Удалить всю очередь: из базы и из системы
     * и пациентов, и маршрутные листы
     *
     * TODO clearQueue?
     */
    fun deleteQueue()

    /**
     * Удаление истории работы кабинетов и статусов пациентов
     */
    fun deleteHistory()
}