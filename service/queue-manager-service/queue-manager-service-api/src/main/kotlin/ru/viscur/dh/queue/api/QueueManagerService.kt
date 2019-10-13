package ru.viscur.dh.queue.api

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
     * Кабинет по id
     */
    fun officeById(officeId: Long): Office

    /**
     * Пациент по id
     */
    fun userById(userId: Long): User

    /**
     * Тип обследования по id
     */
    fun surveyTypeById(surveyTypeId: Long): SurveyType


    /**
     * Пациент получил маршрутный лист: вносим в систему
     */
    fun registerUser(cmd: RegisterUserCMD): RouteSheet

    /**
     * Поставить пациента в очередь
     *
     * TODO moveUserToNextQueue?
     * TODO pass only userId?
     */
    fun addToOfficeQueue(user: User, validate: Boolean = true)

    /**
     * Убрать пациента из очереди
     * Перевод пациента в статус [ru.viscur.dh.queue.api.model.UserInQueueStatus.READY]
     *
     * TODO поговорить с Машей что это за метод и что он делает
     * TODO pass only userId?
     */
    fun deleteFromOfficeQueue(user: User)

    /**
     * Обследование началось
     *
     * TODO pass only userId and officeId? (or create cmd)
     */
    fun surveyStarted(user: User, office: Office);

    /**
     * Обследование закончилось
     *
     * TODO pass only officeId?
     */
    fun surveyFinished(office: Office)

    /**
     * Кабинет готов принять пациента: смена статуса с [OfficeStatus.CLOSED], [OfficeStatus.BUSY] на [OfficeStatus.READY]
     * Если кабинет находится в статусе назначенного пациента, ничего не делаем
     *
     * TODO pass only officeId?
     */
    fun officeIsReady(office: Office);

    /**
     * Смена статуса кабинета с "готов принять" или с "закрыт" на занят
     *
     * TODO pass only officeId?
     */
    fun officeIsBusy(office: Office)

    /**
     * Смена статуса кабинета с "занят" или "готов принять" на "закрыт"
     * Расформировываем очередь
     *
     * TODO pass only officeId?
     */
    fun officeIsClosed(office: Office)

    /**
     * Пациент покинул очередь
     *
     * TODO pass only userId?
     */
    fun userLeftQueue(user: User)

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