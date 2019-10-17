package ru.digitalhospital.queueManager.controller

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 11.09.2019 9:39 by SherbakovaMA
 *
 * Контроллер для очереди
 */

@RestController
@RequestMapping("queue")
class QueueController(private val queueManager: QueueManagerService) {

//    @PostMapping("/registerUser")
//    fun registerUser(@RequestBody registerUserBody: RegisterUserBodyDto) =
//            queueManager.registerUser(registerUserBody.user, registerUserBody.surveys)
//
//    @PostMapping("/survey/started")
//    fun surveyStarted(
//            @RequestParam userId: Long,
//            @RequestParam officeId: Long
//    ) = queueManager.surveyStarted(queueManager.userById(userId), queueManager.officeById(officeId))
//
//    @PostMapping("/survey/finished")
//    fun surveyFinished(@RequestParam officeId: Long) =
//            queueManager.surveyFinished(queueManager.officeById(officeId))
//
//
//    @PostMapping("/office/ready")
//    fun officeIsReady(@RequestParam officeId: Long) =
//            queueManager.officeIsReady(queueManager.officeById(officeId))
//
//    @PostMapping("/office/busy")
//    fun officeIsBusy(@RequestParam officeId: Long) =
//            queueManager.officeIsBusy(queueManager.officeById(officeId))
//
//    @PostMapping("/office/closed")
//    fun officeIsClosed(@RequestParam officeId: Long) =
//            queueManager.officeIsClosed(queueManager.officeById(officeId))
//
//    @PostMapping("/user/addToQueue")
//    fun addToOfficeQueue(
//            @RequestParam userId: Long
//    ) = queueManager.addToOfficeQueue(queueManager.userById(userId))
//
//    @PostMapping("/user/remove")
//    fun removeFromOfficeQueue(
//            @RequestParam userId: Long
//    ) = queueManager.deleteFromOfficeQueue(queueManager.userById(userId))
//
//    @DeleteMapping("/user")
//    fun userLeftQueue(
//            @RequestParam userId: Long
//    ) = queueManager.userLeftQueue(queueManager.userById(userId))
//
//
//    @GetMapping("/surveys")
//    fun notVisitedSurveys(@RequestParam surveyTypeId: Long) =
//            queueManager.routeSheets.flatMap { it.surveys }.filter { it.surveyType.id == surveyTypeId && !it.visited}
//
//    @DeleteMapping
//    fun deleteQueue() = queueManager.deleteQueue()
//
//    @DeleteMapping("/history")
//    fun deleteHistory() = queueManager.deleteHistory()
}
