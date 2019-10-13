package ru.viscur.dh.queue.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.queue.api.UserService
import ru.viscur.dh.queue.api.cmd.RegisterUserCMD
import ru.viscur.dh.queue.api.model.*
import ru.viscur.dh.queue.impl.repository.RouteSheetItemRepository
import ru.viscur.dh.queue.impl.repository.SurveyTypeRepository
import ru.viscur.dh.queue.impl.repository.UserRepository

@Service
class QueueManagerServiceImpl(
        private val userRepository: UserRepository,
        private val officeService: OfficeService,
        private val userService: UserService,
        private val surveyTypeRepository: SurveyTypeRepository,
        private val routeSheetItemRepository: RouteSheetItemRepository
) : QueueManagerService {
    companion object {
        private val LOGGER = LoggerFactory.getLogger(QueueManagerServiceImpl::class.java)
        private val needAutoProcess = false
    }

    var surveyTypes = listOf<SurveyType>()
    val surveyTypesByIds = mapOf<Long, ru.viscur.dh.queue.impl.persistence.model.SurveyTypePE>()
    var offices = listOf<Office>()
    private var users = mutableListOf<User>()
    var routeSheets = mutableListOf<ru.viscur.dh.queue.impl.persistence.model.RouteSheetPE>()
    private var finished = mutableListOf<RouteSheet>()

    override fun officeById(officeId: Long): Office {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun userById(userId: Long): User {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun surveyTypeById(surveyTypeId: Long): SurveyType {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    @Transactional() // TODO разобраться с отдельным менеджером транзакций
    override fun registerUser(cmd: RegisterUserCMD): RouteSheet {
        TODO("Restore");
        /*val user = userRepository.save(ru.viscur.dh.queue.impl.persistence.model.UserPE.fromApiModel(cmd.user))

        val items = cmd.surveys
                .asSequence()
                .map {
                    ru.viscur.dh.queue.impl.persistence.model.RouteSheetItemPE(
                            user = user, // TODO user after save,
                            surveyType = surveyTypesByIds[it.surveyTypeId]
                                    ?: throw Exception("Not found surveyTypeId = ${it.surveyTypeId}"),
                            priority = it.priority
                    )
                }
                .sortedBy {
                    officesForSurveyType(it.surveyType.id)
                            .map { 0 *//* todo *//* }
                            .min()
                }
                .mapIndexed { idx, it -> it.apply { onum = idx } }
                .toList()

        val newRouteSheet = ru.viscur.dh.queue.impl.persistence.model.RouteSheetPE(user, items.toList())

        routeSheets.add(newRouteSheet)
        items.forEach {
            routeSheetItemRepository.save(it)
        }
        //printQueues("user added " + user)// todo
        addToOfficeQueue(cmd.user)
        return newRouteSheet*/
    }

    override fun addToOfficeQueue(user: User, validate: Boolean) {
        TODO("Restore");
        /* if (user.status != UserInQueueStatus.READY) {
             return
         }

         // TODO findLast? может быть несколько???
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
         }*/
    }

    override fun deleteFromOfficeQueue(user: User) {
        TODO("Restore");
        /* //пациент стоит в очереди
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
         printQueues("$user was removed from office queues")// todo*/
    }

    override fun surveyStarted(user: User, office: Office) {
        TODO("Restore");
        /*if (office.firstUserInQueue()?.id == user.id && user.status == UserInQueueStatus.GOING_TO_SURVEY) {
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
        }*/
    }

    override fun surveyFinished(office: Office) {
        TODO("Restore");
        /* val user = office.firstUserInQueue()
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
         }*/
    }

    override fun officeIsReady(office: Office) {
        TODO("Restore");
        /* if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
             officeService.changeStatus(office, OfficeStatus.READY)
             checkEntryToOffice(office)
         }*/
    }

    override fun officeIsBusy(office: Office) {
        TODO("Restore");
/*
        if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
            officeService.changeStatus(office, OfficeStatus.BUSY)
        }*/
    }

    override fun officeIsClosed(office: Office) {
        TODO("Restore");
        /* if (office.status !in listOf(OfficeStatus.SURVEY, OfficeStatus.WAITING_USER)) {
             officeService.changeStatus(office, OfficeStatus.CLOSED)
             office.queue.forEach {
                 userService.changeStatus(it.user, UserInQueueStatus.READY)
                 addToOfficeQueue(it.user, false)
             }
             office.deleteAllFromQueue(queueItemRepository)
             printQueues("closed $office")
         }*/
    }

    override fun userLeftQueue(user: User) {
        TODO("Restore");
        /*deleteFromOfficeQueue(user)
        userService.saveCurrentStatus(user)
        routeSheets = routeSheets.filterNot { it.user.id == user.id }.toMutableList()
        finished = finished.filterNot { it.user.id == user.id }.toMutableList()
        routeSheetItemRepository.deleteAllByUserIs(user.id)
        users.remove(user)
        userRepository.deleteById(user.id)//todo возможно не удалять, или перемещать в отдельную таблицу например archived_users
        printQueues("$user left the system")// todo*/
    }

    override fun deleteQueue() {
        TODO("Restore");
        /*println("delete queue")//todo
        offices.filter { it.status in listOf(OfficeStatus.WAITING_USER, OfficeStatus.SURVEY) }
                .forEach { officeRepository.save(it.apply { status = OfficeStatus.BUSY }) }
        deleteQueueFromDb()
        readQueueFromDb()*/
    }

    override fun deleteHistory() {
        TODO("Restore");
        /*  println("delete history")//todo
          userProcessHistoryRepository.deleteAll()
          officeProcessHistoryRepository.deleteAll()
          */
    }

    private fun officesForSurveyType(surveyTypeId: Long): List<Office> {
        TODO("Not implemented");
    }


    /**
     * Кабинеты с наивысшим приоритетом посещения из непосещенных в маршрутном листе
     */
    private fun officesWithHighestPriority(routeSheet: RouteSheet): List<Office> {
        TODO("Restore");
        /* val notProcessedSurveys = routeSheet.surveys.filterNot { it.visited }
         val maxPriority = notProcessedSurveys.map { it.priority }.max()
         val surveysIdWithMaxPriority = notProcessedSurveys.filter { it.priority == maxPriority }.map { it.surveyType.id }
         return offices.filter { it.surveyType.id in surveysIdWithMaxPriority }*/
    }

}