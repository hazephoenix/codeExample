package ru.viscur.dh.queue.impl.service

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.QueueManagerService
import ru.viscur.dh.queue.api.PatientStatusService
import ru.viscur.dh.queue.api.model.*
import ru.viscur.dh.queue.impl.repository.RouteSheetItemRepository
import ru.viscur.dh.queue.impl.repository.SurveyTypeRepository
import ru.viscur.dh.queue.impl.repository.UserRepository

@Service
class QueueManagerServiceImpl(
        private val userRepository: UserRepository,
        private val officeService: OfficeService,
        private val patientStatusService: PatientStatusService,
        private val patientService: PatientService,
        private val conceptService: ConceptService,
        private val surveyTypeRepository: SurveyTypeRepository,
        private val routeSheetItemRepository: RouteSheetItemRepository,
        private val resourceService: ResourceService
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

    override fun registerUser(patientId: String): List<ServiceRequest> {
        val serviceRequests = patientService.serviceRequests(patientId)
                .sortedWith(
                        compareBy({
                            -priority(it)
                        }, {
                            -estWaitingInQueueWithType(patientId, it)
                        })
                )
        serviceRequests.forEachIndexed { index, serviceRequest ->
            serviceRequest.extension.executionOrder = index
            resourceService.update(serviceRequest)
        }
        addToOfficeQueue(patientId)
        return serviceRequests
    }

    private fun estDuration(patientId: String, officeId: String): Int {
        //todo это диагноз пациента + тип пациента + типы процедур в этом кабинете -> продолжительность обслуживания. зашитое сопоставление
        return 1000
    }

    /**
     * Предположительное время ожидания в очереди пациента с типом [type] =
     * Сумма приблизительных продолжительностей осмотра всех пациентов перед позицией в очереди, куда бы встал пациент с типом [type]
     */
    private fun estWaitingInQueueWithType(patientId: String, it: ServiceRequest): Int {
        //todo estWaitingInQueueWithType
        return 1000
    }

    //todo serv req -> code -> parentCode -> priority
    private fun priority(serviceRequest: ServiceRequest): Double {
        if (!serviceRequest.performer.isNullOrEmpty()) {
            return 0.0
        }
        val observationType = conceptService.byCodeableConcept(serviceRequest.code)
        observationType.parentCode ?: throw Exception("Observation type ${serviceRequest.code} has no parentCode")
        val observationCategory = conceptService.parent(observationType)!!
        return observationCategory.priority ?: 0.5
    }

    override fun addToOfficeQueue(patientId: String) {
        val nextOfficeId = nextOfficeId(patientId)
        nextOfficeId?.run {
            officeService.addPatientToQueue(patientId, nextOfficeId, estDuration(patientId, nextOfficeId))
            patientStatusService.changeStatus(patientId, PatientQueueStatus.IN_QUEUE)
            checkEntryToOffice(nextOfficeId)
        } ?: run {
            //завершил выполнение
        }
    }

    /**
     * Проверить вход в кабинет: если есть возможность запускаем первого в очереди
     */
    private fun checkEntryToOffice(officeId: String? = null, office: Location? = null) {
        val office = office ?: office(officeId!!)
        if (office.status == LocationStatus.READY) {
            sendFirstToSurvey(office)
        }
    }

    private fun office(officeId: String): Location {
//        todo получение Location по id.
        val location = resourceService.byId(ResourceType.Location, officeId)
        return Location(name = "")
    }

    /**
     * Отправить первого в очереди в кабинет
     */
    private fun sendFirstToSurvey(office: Location) {
        val patientId = officeService.firstPatientInQueue(office.id!!)
        patientId?.run {
            officeService.changeStatus(office, LocationStatus.WAITING_USER)
            patientStatusService.changeStatus(patientId, PatientQueueStatus.GOING_TO_SURVEY, office.id)
            //todo уведомить о необходимоси пройти в кабинет
        }
    }

    private fun nextOfficeId(patientId: String): String? {
        //todo active clinicalImpression -> not completed serviceRequests -> min by executionOrder
        return "werew"
    }

    override fun deleteFromOfficeQueue(patientId: String) {
        /* val patient = patient(patientId)!!
         //пациент стоит в очереди
         if (patient.extension.queueStatus !in listOf(PatientQueueStatus.FINISHED, PatientQueueStatus.READY)) {
             //пациент в очереди (ожидает, идет на обслед. или на обслед.) - необходимо удалить из очереди, освободить если нужно кабинет
             val office = isUserInOfficeQueue(patient)!!
             //его ожидают в кабинете или идет осмотр - освободим кабинет
             if (patient.extension.queueStatus in listOf(PatientQueueStatus.GOING_TO_SURVEY, PatientQueueStatus.ON_SURVEY)) {
                 officeService.changeStatus(office, LocationStatus.READY)
                 officeService.deleteFirstPatientFromQueue(office)
                 checkEntryToOffice(office = office)
             } else {
                 //пациент просто в очереди. его очередь не настала. просто удаляем из очереди
                 officeService.deletePatientFromQueue(office, patient)
             }
             //если прервано обследование, то не записываем в историю продолжительность
             val saveCurrentStatusToHistory = patient.status != PatientQueueStatus.ON_SURVEY
             patientService.changeStatus(patient, PatientQueueStatus.READY, office.id, saveCurrentStatusToHistory)
         }
         deleteUserFromLastUserInfo(patient)
         printQueues("$patient was removed from office queues")// todo*/
    }

    private fun patient(id: String): Patient? {
//        ResourceType<Patient>.Companion.byId(ResourceType.Patient.id)
        //todo patient po id
        return null
    }

    /**
     * Стоит ли пациент в очереди к какому-нибудь кабинету. Если да, то возвращается найденный кабинет
     */
    private fun isUserInOfficeQueue(user: Patient): Location? {
        //todo найти QueueItem с userId. взять Ref(location), конвертнуть в Location
        return null
    }

    override fun surveyStarted(user: User, office: Office) {
        TODO("Restore");
        /*if (office.firstUserInQueue()?.id == user.id && user.status == PatientQueueStatus.GOING_TO_SURVEY) {
            officeService.changeStatus(office, LocationStatus.SURVEY, user)
            userService.changeStatus(user, PatientQueueStatus.ON_SURVEY, office.id)
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
         if (user?.status == PatientQueueStatus.ON_SURVEY) {
             officeService.changeStatus(office, LocationStatus.BUSY, user)
             val visitedRouteSheetItem = routeSheets.find { it.user.id == user.id }!!.surveys.find { it.surveyType.id == office.surveyType.id }!!
             visitedRouteSheetItem.visited = true
             routeSheetItemRepository.save(visitedRouteSheetItem)
             office.deleteFirstUserFromQueue(queueItemRepository)
             userService.changeStatus(user, PatientQueueStatus.READY, office.id)
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
        /* if (office.status !in listOf(LocationStatus.SURVEY, LocationStatus.WAITING_USER)) {
             officeService.changeStatus(office, LocationStatus.READY)
             checkEntryToOffice(office)
         }*/
    }

    override fun officeIsBusy(office: Office) {
        TODO("Restore");
/*
        if (office.status !in listOf(LocationStatus.SURVEY, LocationStatus.WAITING_USER)) {
            officeService.changeStatus(office, LocationStatus.BUSY)
        }*/
    }

    override fun officeIsClosed(office: Office) {
        TODO("Restore");
        /* if (office.status !in listOf(LocationStatus.SURVEY, LocationStatus.WAITING_USER)) {
             officeService.changeStatus(office, LocationStatus.CLOSED)
             office.queue.forEach {
                 userService.changeStatus(it.user, PatientQueueStatus.READY)
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
        offices.filter { it.status in listOf(LocationStatus.WAITING_USER, LocationStatus.SURVEY) }
                .forEach { officeRepository.save(it.apply { status = LocationStatus.BUSY }) }
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