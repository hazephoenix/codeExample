package ru.digitalhospital.queueManager.service

import org.springframework.stereotype.Service
import ru.digitalhospital.queueManager.SECONDS_IN_MINUTE
import ru.digitalhospital.queueManager.ageGroup
import ru.digitalhospital.queueManager.dto.OfficeStatus
import ru.digitalhospital.queueManager.dto.RouteSheet
import ru.digitalhospital.queueManager.dto.UserInQueueStatus
import ru.digitalhospital.queueManager.dto.UserType
import ru.digitalhospital.queueManager.entities.Office
import ru.digitalhospital.queueManager.entities.RouteSheetItem
import ru.digitalhospital.queueManager.entities.SurveyType
import ru.digitalhospital.queueManager.entities.User
import ru.digitalhospital.queueManager.now
import ru.digitalhospital.queueManager.repository.*
import ru.digitalhospital.queueManager.toStringFmtWithSeconds
import javax.annotation.PostConstruct

/**
 * Created at 03.09.2019 13:00 by SherbakovaMA
 *
 * todo
 */
@Service
class QueueManager(
        private val officesQueueRepository: QueueItemRepository,
        private val officeRepository: OfficeRepository,
        private val queueItemRepository: QueueItemRepository,
        private val routeSheetItemRepository: RouteSheetItemRepository,
        private val surveyTypeRepository: SurveyTypeRepository,
        private val userRepository: UserRepository,
        private val userService: UserService,
        private val officeService: OfficeService,

        private val userProcessHistoryRepository: UserProcessHistoryRepository,
        private val officeProcessHistoryRepository: OfficeProcessHistoryRepository


) {

    var surveyTypes = listOf<SurveyType>()

    var offices = listOf<Office>()

    private var users = mutableListOf<User>()

    var routeSheets = mutableListOf<RouteSheet>()

    private var finished = mutableListOf<RouteSheet>()

    /**
     * Для отладки: нужен ли авто запуск некоторых процессов
     * Эмуляция окончания обследования, событие захода пациента для обследования, событие сообщения о том, что кабинет готов принять и т.д.
     * тех событий, которые будут инициироваться из-вне пациентами и сотрудниками мед. учреждения
     */
    companion object {
        private val needAutoProcess = false

    }

    @PostConstruct
    fun postConstruct() {

//        val st1 = SurveyType(1, "hirurg")
//        val st2 = SurveyType(2, "lor")
//        val st3 = SurveyType(3, "uzi")
//        val st4 = SurveyType(4, "rentgen")
//        val st5 = SurveyType(5, "krov")
//        val st6 = SurveyType(6, "mocha")
//        surveyTypeRepository.save(st1)
//        surveyTypeRepository.save(st2)
//        surveyTypeRepository.save(st3)
//        surveyTypeRepository.save(st4)
//        surveyTypeRepository.save(st5)
//        surveyTypeRepository.save(st6)
//
//        officeRepository.deleteAll()
//
//        officeRepository.save(Office(1, "hirurg", st1))
//        officeRepository.save(Office(2, "lor", st2))
//        officeRepository.save(Office(3, "uzi", st3))
//        officeRepository.save(Office(4, "rentgen", st4))
//        officeRepository.save(Office(5, "krov", st5))
//        officeRepository.save(Office(6, "krov", st5))
//        officeRepository.save(Office(7, "mocha", st6))

        deleteHistory()

//        deleteQueueFromDb()
//        offices.forEach { officeRepository.save(it.apply { status = OfficeStatus.READY }) }
        surveyTypes = surveyTypeRepository.findAll().toList()

        readQueueFromDb()

//        registerUser(User(firstName = "u1", lastName = "1", type = UserType.GREEN, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0)))
//        GlobalScope.async { registerUser(User(firstName = "u1", lastName = "1", type = UserType.GREEN, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u2", lastName = "1", type = UserType.GREEN, birthDate = date("1.09.1970 20:00"), diagnostic = "d3"), listOf(Pair(1L, 0.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u3", lastName = "1", type = UserType.YELLOW, birthDate = date("1.09.1950 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0), Pair(6L, 1.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u4", lastName = "1", type = UserType.RED, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0), Pair(6L, 1.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u5", lastName = "1", type = UserType.RED, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0), Pair(6L, 1.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u6", lastName = "1", type = UserType.YELLOW, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0), Pair(6L, 1.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u7", lastName = "1", type = UserType.YELLOW, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"), listOf(Pair(1L, 0.0), Pair(6L, 1.0))) }
//        GlobalScope.async { registerUser(User(firstName = "u8", lastName = "2", type = UserType.GREEN, birthDate = date("1.09.2010 20:00"), diagnostic = "d2"), listOf(Pair(2L, 0.0), Pair(5L, 0.0), Pair(6L, 1.0))) }
//        Thread.sleep(2000)
//        offices.forEach { officeIsReady(it) }

    }

    /**
     * Пациент получил маршрутный лист: вносим в систему
     */
    fun registerUser(user: User, surveysWithPriorities: List<Pair<Long, Double>>): RouteSheet {
        userRepository.save(user.apply { status = UserInQueueStatus.READY })
        users.add(user)
        val routeSheetItems = surveysWithPriorities.map { (surveyTypeId, priority) ->
            if (surveyTypes.none { surveyType -> surveyType.id == surveyTypeId }) {
                throw Exception("Not found surveyTypeId = $surveyTypeId")
            }
            RouteSheetItem(user = user, surveyType = surveyTypes.find { surveyType -> surveyType.id == surveyTypeId }!!, priority = priority)
        }.sortedBy {
            it.let { routeSheetItem ->
                offices.filter { it.surveyType.id == routeSheetItem.surveyType.id }
                        .map { it.estWaitingInQueueWithType(routeSheetItem.user.type) }.min()
            }

        }
                .mapIndexed { i, it -> it.apply { onum = i } }
        val newRouteSheet = RouteSheet(
                user,
                routeSheetItems
        )
        routeSheets.add(newRouteSheet)
        routeSheetItems.forEach {
            routeSheetItemRepository.save(it)
        }
        printQueues("user added " + user)// todo
        addToOfficeQueue(user)
        return newRouteSheet
    }

    /**
     * Поставить пациента в очередь
     */
    fun addToOfficeQueue(user: User, validate: Boolean = true) {
        if (user.status != UserInQueueStatus.READY) return

        val routeSheet = routeSheets.findLast { it.user.id == user.id }!!
        var officesForQueue = officesWithHighestPriority(routeSheet)
        if (officesForQueue.isEmpty()) {
            userService.changeStatus(user, UserInQueueStatus.FINISHED)
            routeSheets.minusAssign(routeSheet)
            finished.add(routeSheet)
            printQueues("user sent to finished routeSheets " + user)// todo
            if (needAutoProcess) {
//                GlobalScope.async {
                //todo auto
                Thread.sleep(3000)
                userLeftQueue(user)
//                }
            }
            //todo можно сообщить о пополнении в finished
        } else {
            val userType = user.type
            officesForQueue = officesForQueue.filter { it.status != OfficeStatus.CLOSED }
            if (officesForQueue.isEmpty()) {
                println("all offices with high priority are closed for $user")
                return
            }
            val nextOffice =
                    if (userType == UserType.GREEN) {
                        //зеленые подсчитывают время всех в очереди
                        officesForQueue.minBy { it.totalEstWaitingInQueue() }!!
                    } else {
                        //желтые и красные подсчитывают время только тех, кого не пропускают
                        val officesForQueueWithEstDurByType = officesForQueue.map { Pair(it, it.estWaitingInQueueWithType(userType)) }
                        val minEstDurByType = officesForQueueWithEstDurByType.minBy { it.second }?.second
                        val officesWithMinEstDurByType = officesForQueueWithEstDurByType.filter { it.second == minEstDurByType }.map { it.first }
                        //если по типу определился однозначно минимальная очередь
                        if (officesWithMinEstDurByType.size == 1) {
                            officesWithMinEstDurByType.first()
                        } else {
                            //если по типу несколько кабинетов с минимальным ожиданием
                            // то в учет идет также общая очередь вне зависимости от типа
                            officesWithMinEstDurByType.minBy { it.totalEstWaitingInQueue() }!!
                        }
                    }
            val estDuration = calcEstDuration(user, nextOffice)
            nextOffice.addUserToQueue(user, estDuration, queueItemRepository)
            userService.changeStatus(user, UserInQueueStatus.IN_QUEUE)
            checkEntryToOffice(nextOffice)
            printQueues("$user added to office queue " + nextOffice, validate)// todo
        }
    }

    /**
     * Убрать пациента из очереди
     * Перевод пациента в статус [UserInQueueStatus.READY]
     */
    fun deleteFromOfficeQueue(user: User) {
        //пациент стоит в очереди
        if (user.status !in listOf(UserInQueueStatus.FINISHED, UserInQueueStatus.READY)) {
            //пациент в очереди (ожидает, идет на обслед. или на обслед.) - необходимо удалить из очереди, освободить если нужно кабинет
            val office = isUserInOfficeQueue(user)!!
            //его ожидают в кабинете или идет осмотр - освободим кабинет
            if (user.status in listOf(UserInQueueStatus.GOING_TO_SURVEY, UserInQueueStatus.ON_SURVEY)) {
                officeService.changeStatus(office, OfficeStatus.READY)
                office.deleteFirstUserFromQueue(queueItemRepository)
                checkEntryToOffice(office)
            } else {
                //пациент просто в очереди. его очередь не настала. просто удаляем из очереди
                office.deleteUserFromQueue(user, queueItemRepository)
            }
            //если прервано обследование, то не записываем в историю продолжительность
            val saveCurrentStatusToHistory = user.status != UserInQueueStatus.ON_SURVEY
            userService.changeStatus(user, UserInQueueStatus.READY, office.id, saveCurrentStatusToHistory)
        }
        deleteUserFromLastUserInfo(user)
        printQueues("$user was removed from office queues")// todo
    }

    /**
     * Обследование началось
     */
    fun surveyStarted(user: User, office: Office) {
        if (office.firstUserInQueue()?.id == user.id && user.status == UserInQueueStatus.GOING_TO_SURVEY) {
            officeService.changeStatus(office, OfficeStatus.SURVEY, user)
            userService.changeStatus(user, UserInQueueStatus.ON_SURVEY, office.id)
            deleteUserFromLastUserInfo(user)
            printQueues("survey started of user " + user + " in office " + office)// todo
            if (needAutoProcess) {
//            GlobalScope.async {
                //todo auto
                Thread.sleep(3000)
                surveyFinished(office)
//            }
            }
        }
    }

    /**
     * Обследование закончилось
     */
    fun surveyFinished(office: Office) {
        val user = office.firstUserInQueue()
        if (user?.status == UserInQueueStatus.ON_SURVEY) {
            officeService.changeStatus(office, OfficeStatus.BUSY, user)
            val visitedRouteSheetItem = routeSheets.find { it.user.id == user.id }!!.surveys.find { it.surveyType.id == office.surveyType.id }!!
            visitedRouteSheetItem.visited = true
            routeSheetItemRepository.save(visitedRouteSheetItem)
            office.deleteFirstUserFromQueue(queueItemRepository)
            userService.changeStatus(user, UserInQueueStatus.READY, office.id)
            printQueues("survey finished for user " + user + " in office " + office)// todo
            addToOfficeQueue(user)
            if (users.any { it.id == user.id }) {
                office.lastUserInfo = Pair(user, isUserInOfficeQueue(user))
            }
            printQueues("lastUserInfo updated for " + office)// todo
            if (needAutoProcess) {
//            GlobalScope.async {
                //todo auto
                Thread.sleep(2000)
                officeIsReady(office)
//            }
            }
        }
    }

    /**
     * Кабинет готов принять пациента: смена статуса с [OfficeStatus.CLOSED], [OfficeStatus.BUSY] на [OfficeStatus.READY]
     * Если кабинет находится в статусе назначенного пациента, ничего не делаем
     */
    fun officeIsReady(office: Office) {
        if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
            officeService.changeStatus(office, OfficeStatus.READY)
            checkEntryToOffice(office)
        }
    }

    /**
     * Смена статуса кабинета с "готов принять" или с "закрыт" на занят
     */
    fun officeIsBusy(office: Office) {
        if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
            officeService.changeStatus(office, OfficeStatus.BUSY)
        }
    }

    /**
     * Смена статуса кабинета с "занят" или "готов принять" на "закрыт"
     * Расформировываем очередь
     */
    fun officeIsClosed(office: Office) {
        if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
            officeService.changeStatus(office, OfficeStatus.CLOSED)
            office.queue.forEach {
                userService.changeStatus(it.user, UserInQueueStatus.READY)
                addToOfficeQueue(it.user, false)
            }
            office.deleteAllFromQueue(queueItemRepository)
            printQueues("closed $office")
        }
    }

    /**
     * Пациент покинул очередь
     */
    fun userLeftQueue(user: User) {
        deleteFromOfficeQueue(user)
        userService.saveCurrentStatus(user)
        routeSheets = routeSheets.filterNot { it.user.id == user.id }.toMutableList()
        finished = finished.filterNot { it.user.id == user.id }.toMutableList()
        routeSheetItemRepository.deleteAllByUserIs(user.id)
        users.remove(user)
        userRepository.deleteById(user.id)//todo возможно не удалять, или перемещать в отдельную таблицу например archived_users
        printQueues("$user left the system")// todo
    }

    /**
     * Кабинет по id
     */
    fun officeById(officeId: Long) = offices.find { it.id == officeId }
            ?: throw Exception("not found office with id $officeId")

    /**
     * Пациент по id
     */
    fun userById(userId: Long) = users.find { it.id == userId }
            ?: throw Exception("not found user with id $userId")

    /**
     * Тип обследования по id
     */
    fun surveyTypeById(surveyTypeId: Long) = surveyTypes.find { it.id == surveyTypeId }
            ?: throw Exception("not found survey type with id $surveyTypeId")

    /**
     * Удалить всю очередь: из базы и из системы
     * и пациентов, и маршрутные листы
     */
    fun deleteQueue() {
        println("delete queue")//todo
        offices.filter { it.status in listOf(OfficeStatus.WAITING_USER, OfficeStatus.SURVEY) }
                .forEach { officeRepository.save(it.apply { status = OfficeStatus.BUSY }) }
        deleteQueueFromDb()
        readQueueFromDb()
    }

    /**
     * Удаление истории работы кабинетов и статусов пациентов
     */
    fun deleteHistory() {
        println("delete history")//todo
        userProcessHistoryRepository.deleteAll()
        officeProcessHistoryRepository.deleteAll()
    }

    /**
     * Удаление сохраненной очереди в базе
     */
    private fun deleteQueueFromDb() {
        routeSheetItemRepository.deleteAll()
        queueItemRepository.deleteAll()
        userRepository.deleteAll()
    }

    private fun readQueueFromDb() {
        offices = officeRepository.findAll().toList()
        offices.forEach { it.surveyType = surveyTypeById(it.surveyType.id) }

        val routeSheetItems = routeSheetItemRepository.findAllByOrderByUserAscOnumAsc()
        val allRouteSheets = routeSheetItems.groupBy { it.user }.map { (user, routeSheetItems) ->
            RouteSheet(user, routeSheetItems)
        }
        routeSheets = allRouteSheets.filter { it.surveys.any { !it.visited } }.toMutableList()
        finished = allRouteSheets.filter { it.surveys.all { it.visited } }.toMutableList()
        users = allRouteSheets.map { it.user }.toMutableList()

        val allQueueItems = queueItemRepository.findAllByOrderByOfficeAscOnumAsc()
        allQueueItems.forEach { it.user = userById(it.user.id) }
        allQueueItems.groupBy { it.office }.forEach { (office, queueItems) ->
            val foundOffice = offices.find { it.id == office.id }
            foundOffice?.queue = queueItems.toMutableList()
        }
        printQueues("after reading from db")
    }

    /**
     * Определение предположительной продолжительности обслуживания пациента в кабинете
     */
    private fun calcEstDuration(user: User, office: Office): Int {
        val surveyTypeId = office.surveyType.id

        //вычисляем среднюю продолжительность осмотра по накопившейся истории приема этого типа обследования + 3 характеристики
        // если нет конкретного совпадения тип обсл + 3 характеристики, то расширяем поиск по 2 характеристикам, затем по 1 характеристике
        // если и таковых нет (тип обсл + 1 хар-ка), то берем фиксированно для всех типов обсл: желтый одно время, зеленый - другое, красный - третье
        val userType = user.type.toString()
        val userDiagnostic = user.diagnostic!!
        val userAgeGroup = ageGroup(user.birthDate)
        return (officeProcessHistoryRepository.avgBySurveyTypeIdAndUserTypeAndUserDiagnosticAndUserAgeGroup(surveyTypeId, userType, userDiagnostic, userAgeGroup)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserTypeAndUserDiagnostic(surveyTypeId, userType, userDiagnostic)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserTypeAndUserAgeGroup(surveyTypeId, userType, userAgeGroup)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserDiagnosticAndUserAgeGroup(surveyTypeId, userDiagnostic, userAgeGroup)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserType(surveyTypeId, userType)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserDiagnostic(surveyTypeId, userDiagnostic)
                ?: officeProcessHistoryRepository.avgBySurveyTypeIdAndUserAgeGroup(surveyTypeId, userAgeGroup)
                )
                ?.toInt()
                ?: when (user.type) {
                    UserType.GREEN -> 5 * SECONDS_IN_MINUTE
                    UserType.YELLOW -> 10 * SECONDS_IN_MINUTE
                    UserType.RED -> 15 * SECONDS_IN_MINUTE
                }
    }

    /**
     * Кабинеты с наивысшим приоритетом посещения из непосещенных в маршрутном листе
     */
    private fun officesWithHighestPriority(routeSheet: RouteSheet): List<Office> {
        val notProcessedSurveys = routeSheet.surveys.filterNot { it.visited }
        val maxPriority = notProcessedSurveys.map { it.priority }.max()
        val surveysIdWithMaxPriority = notProcessedSurveys.filter { it.priority == maxPriority }.map { it.surveyType.id }
        return offices.filter { it.surveyType.id in surveysIdWithMaxPriority }
    }

    /**
     * Проверить вход в кабинет: если есть возможность запускаем первого в очереди
     */
    private fun checkEntryToOffice(office: Office) {
        if (office.status == OfficeStatus.READY) {
            sendFirstToSurvey(office)
        }
    }

    /**
     * Отправить первого в очереди в кабинет
     */
    private fun sendFirstToSurvey(office: Office) {
        val user = office.firstUserInQueue()
        user?.run {
            officeService.changeStatus(office, OfficeStatus.WAITING_USER)
            userService.changeStatus(user, UserInQueueStatus.GOING_TO_SURVEY, office.id)
            printQueues("$user sent to survey to " + office)// todo
            //todo уведомить о необходимоси пройти в кабинет
            if (needAutoProcess) {
//                GlobalScope.async {
                //todo auto
                Thread.sleep(1000)
                surveyStarted(user, office)
//                }
            }
        }
    }

    /**
     * Стоит ли пациент в очереди к какому-нибудь кабинету. Если да, то возвращается найденный кабинет
     */
    private fun isUserInOfficeQueue(user: User): Office? = offices.firstOrNull { it.queue.any { it.user.id == user.id } }

    /**
     * Удаление пациента из информации о последнем пациенте (если он фигурирует в такой информации в каком-либо кабинете)
     */
    private fun deleteUserFromLastUserInfo(user: User) {
        offices.filter { it.lastUserInfo?.first?.id == user.id }.forEach { it.lastUserInfo = null }
    }

    private fun printQueues(comment: String = "", validate: Boolean = true) {
        println(now().toStringFmtWithSeconds() + ". " + comment)
        var str = "offices:\n"
        offices.forEach { office ->
            str += "  ${office.id}(${office.surveyType.id}) ${office.status} .    " + office + "\n"
            office.queue.forEach { qItem ->
                str += "    " + qItem.user + ". qiId: ${qItem.id}, onum: ${qItem.onum}, estDur:" + qItem.estDuration + "\n"
            }
            office.lastUserInfo?.run {
                str += "    lastUserInfo: " + (office.lastUserInfo?.first ?: "") + ", " +
                        (office.lastUserInfo?.second ?: "") + "\n"
            }
        }
        str += "routeSheets:\n  " + routeSheets.joinToString("\n  ")
        str += "\nfinished:\n  " + finished.joinToString("\n  ")
        println(str)
        if (validate) {
            validQueue()
        }
    }

    private fun validQueue() {
        offices.forEach { office ->
            if (office.queue.isEmpty()) {
                //при пустой очереди кабинет не должен иметь назначенного пациента
                if (office.status in listOf(OfficeStatus.WAITING_USER, OfficeStatus.SURVEY)) {
                    println("ERROR. there is no users in queue, but $office has active status")
                    throw Exception("ERROR. there is no users in queue, but $office has active status")
                }
                return@forEach
            }
            val firstUser = office.firstUserInQueue()
            //статус первого в очереди
            if (firstUser!!.status !in listOf(UserInQueueStatus.ON_SURVEY, UserInQueueStatus.GOING_TO_SURVEY, UserInQueueStatus.IN_QUEUE)) {
                println("ERROR. first user in queue to office $office has wrong status ${firstUser.status}")
                throw Exception("ERROR. first user in queue to office $office has wrong status ${firstUser.status}")
            }
            //статусы всех кроме первого в очереди
            if (office.queue.filterIndexed { i, _ -> i > 0 }.any { it.user.status != UserInQueueStatus.IN_QUEUE }) {
                println("ERROR. all users except first must have status IN_QUEUE. Office with error: $office")
                throw Exception("ERROR. all users except first must have status IN_QUEUE. Office with error: $office")
            }
            //один пациент не может стоять в очереди несколько раз в один офис
            office.queue.groupBy { it.user.id }.forEach { userId, queueItems ->
                if (queueItems.size > 1) {
                    println("ERROR. user with id $userId is in several queue items to $office")
                    throw Exception("ERROR. user with id $userId is in several queue items to $office")
                }
            }
            //если стоит в очереди то в его маршрутном листе visited = false для этого типа обсл.
            office.queue.forEach { queueItem ->
                val routeSheet = routeSheets.find { it.user.id == queueItem.user.id }
                        ?: throw Exception("ERROR. ${queueItem.user} in queue of $office but there is no his routeSheet")
                val routeSheetItem = routeSheet.surveys.find { it.surveyType.id == office.surveyType.id }
                        ?: throw Exception("ERROR. ${queueItem.user} in queue of $office but there is no ${office.surveyType} in his $routeSheet")
                if (routeSheetItem.visited) {
                    println("ERROR. ${queueItem.user} in queue of $office but he already visited it ($routeSheet)")
                    throw Exception("ERROR. ${queueItem.user} in queue of $office but he already visited it ($routeSheet)")
                }
            }
            //совпадение статуса кабинета и статуса первого в очереди
            if (office.status == OfficeStatus.WAITING_USER) {
                if (firstUser.status != UserInQueueStatus.GOING_TO_SURVEY) {
                    println("ERROR. $office is waiting user but first user has wrong status: $firstUser")
                    throw Exception("ERROR. $office is waiting user but first user has wrong status: $firstUser")
                }
            } else if (office.status == OfficeStatus.SURVEY) {
                if (firstUser.status != UserInQueueStatus.ON_SURVEY) {
                    println("ERROR. $office has survey status but first user has wrong status: $firstUser")
                    throw Exception("ERROR. $office has survey status but first user has wrong status: $firstUser")
                }
            } else if (firstUser.status != UserInQueueStatus.IN_QUEUE) {
                println("ERROR. $office has one of inactive status but first user has wrong status: $firstUser")
                throw Exception("ERROR. $office has one of inactive status but first user has wrong status: $firstUser")
            }
        }
        //один пациент не может стоять в несколько очередей в разные кабинеты
        offices.flatMap { it.queue }.groupBy { it.user.id }.forEach { (userId, queueItems) ->
            if (queueItems.size > 1) {
                println("ERROR. user with id $userId is in several office queues")
                throw Exception("ERROR. user with id $userId is in several office queues")
            }
        }
        //один пациент не должен отображаться в информации о посл пациенте в неск кабинетах
        offices.mapNotNull { it.lastUserInfo?.first }.groupBy { it.id }.forEach { (userId, offices) ->
            if (offices.size > 1) {
                println("ERROR. user with id $userId is in several office queues")
                throw Exception("ERROR. user with id $userId is in several lastUserInfo $offices")
            }
        }

        //соответствие состояния очереди в приложении и в базе
        val queueItemsInDb = queueItemRepository.findAll().toList()
        if (offices.flatMap { it.queue }.size != queueItemsInDb.size) {
            println("ERROR. wrong number of queue items in db (${queueItemsInDb.size}). must be ${offices.flatMap { it.queue }.size}")
            throw Exception("ERROR. wrong number of queue items in db (${queueItemsInDb.size}). must be ${offices.flatMap { it.queue }.size}")
        }
        offices.forEach { office ->
            office.queue.forEachIndexed { i, queueItem ->
                val queueItemsInDb = queueItemRepository.findByOfficeIdAndUserId(office.id, queueItem.user.id)
                if (queueItemsInDb.size > 1) {
                    println("ERROR. there are several queue items with ${queueItem.user} and $office")
                    throw Exception("ERROR. there are several queue items with ${queueItem.user} and $office")
                } else if (queueItemsInDb.isEmpty()) {
                    println("ERROR. not found $queueItem in db by user and $office")
                    throw Exception("ERROR. not found $queueItem in db by user and $office")
                }
                val queueItemInDb = queueItemsInDb.first()
                if (queueItem.estDuration != queueItemInDb.estDuration) {
                    println("ERROR. $queueItem has wrong estDuration in db ($queueItemInDb)")
                    throw Exception("ERROR. $queueItem has wrong estDuration in db ($queueItemInDb)")
                }
                if (queueItem.user.id != queueItemInDb.user.id) {
                    println("ERROR. $queueItem has wrong user in db ($queueItemInDb)")
                    throw Exception("ERROR. $queueItem has wrong user in db ($queueItemInDb)")
                }
                if (i != queueItemInDb.onum) {
                    println("ERROR. $queueItem has wrong onum in db ($queueItemInDb)")
                    throw Exception("ERROR. $queueItem has wrong onum in db ($queueItemInDb)")
                }
            }
        }
        //Соотсветствие маршрутных листов в базе и в приложении
        val rsiInDb = routeSheetItemRepository.findAll().toList()
        val allRouteSheets = routeSheets + finished
        if (allRouteSheets.flatMap { it.surveys }.size != rsiInDb.size) {
            println("ERROR. wrong number of route sheets items in db (${rsiInDb.size}). must be ${allRouteSheets.flatMap { it.surveys }.size}")
            throw Exception("ERROR. wrong number of route sheets items in db (${rsiInDb.size}). must be ${allRouteSheets.flatMap { it.surveys }.size}")
        }
        allRouteSheets.forEach { routeSheet ->
            routeSheet.surveys.forEachIndexed { i, routeSheetItem ->
                val rsitemsOfUserInDb = routeSheetItemRepository.findBySurveyTypeIdAndUserId(routeSheetItem.surveyType.id, routeSheet.user.id)
                if (rsitemsOfUserInDb.size > 1) {
                    throw Exception("ERROR. there are several routeSheet items with ${routeSheet.user} and ${routeSheetItem.surveyType}")
                } else if (rsitemsOfUserInDb.isEmpty()) {
                    println("ERROR. not found $routeSheet in db by ${routeSheet.user} and ${routeSheetItem.surveyType}")
                    throw Exception("ERROR. not found $routeSheet in db by ${routeSheet.user} and ${routeSheetItem.surveyType}")
                }
                val rsitemOfUserInDb = rsitemsOfUserInDb.first()
                if (routeSheetItem.priority != rsitemOfUserInDb.priority) {
                    println("ERROR. $routeSheetItem has wrong proirity in db ($rsitemOfUserInDb)")
                    throw Exception("ERROR. $routeSheetItem has wrong proirity in db ($rsitemOfUserInDb)")
                }
                if (routeSheetItem.visited != rsitemOfUserInDb.visited) {
                    println("ERROR. $routeSheetItem has wrong visited value in db ($rsitemOfUserInDb)")
                    throw Exception("ERROR. $routeSheetItem has wrong visited value in db ($rsitemOfUserInDb)")
                }
                if (i != rsitemOfUserInDb.onum) {
                    println("ERROR. $routeSheetItem has wrong onum in db ($rsitemOfUserInDb)")
                    throw Exception("ERROR. $routeSheetItem has wrong onum in db ($rsitemOfUserInDb)")
                }
            }
        }
        //в finished д б пациенты со всеми пройденными обследованиями
        finished.forEach { routeSheet ->
            if (routeSheet.surveys.any { !it.visited }) {
                println("ERROR. $routeSheet has not visited items but it's in finished list")
                throw Exception("ERROR. $routeSheet has not visited items but it's in finished list")
            }
        }
        //Порядок очереди: кр, ж, з
        offices.forEach { office ->
            val lastRed = office.queue.indexOfLast { it.user.status == UserInQueueStatus.IN_QUEUE && it.user.type == UserType.RED }
            val firstYellow = office.queue.indexOfFirst { it.user.status == UserInQueueStatus.IN_QUEUE && it.user.type == UserType.YELLOW }
            val lastYellow = office.queue.indexOfLast { it.user.status == UserInQueueStatus.IN_QUEUE && it.user.type == UserType.YELLOW }
            val firstGreen = office.queue.indexOfFirst { it.user.status == UserInQueueStatus.IN_QUEUE && it.user.type == UserType.GREEN }
            if (firstYellow != -1 && lastRed != -1 && lastRed > firstYellow) {
                println("ERROR. in $office index of yellow type user ($firstYellow) is less than index of red type user ($lastRed)")
                throw Exception("ERROR. in $office index of yellow type user ($firstYellow) is less than index of red type user ($lastRed)")
            }
            if (firstGreen != -1 && lastYellow != -1 && lastYellow > firstGreen) {
                println("ERROR. in $office index of green type user ($firstYellow) is less than index of yellow type user ($lastYellow)")
                throw Exception("ERROR. in $office index of green type user ($firstYellow) is less than index of yellow type user ($lastYellow)")
            }
        }
    }
}