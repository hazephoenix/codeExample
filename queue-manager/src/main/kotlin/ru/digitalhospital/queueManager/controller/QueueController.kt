package ru.digitalhospital.queueManager.controller

import org.springframework.web.bind.annotation.*
import ru.digitalhospital.queueManager.dto.RegisterUserBodyDto
import ru.digitalhospital.queueManager.service.QueueManager

/**
 * Created at 11.09.2019 9:39 by SherbakovaMA
 *
 * Контроллер для очереди
 */
@RestController
@RequestMapping("queue")
class QueueController(private val queueManager: QueueManager) {

    /**
     * see [QueueManager.registerUser]
     */
    @PostMapping("/registerUser")
    fun registerUser(@RequestBody registerUserBody: RegisterUserBodyDto) =
            queueManager.registerUser(registerUserBody.user, registerUserBody.surveys)

    /**
     * see [QueueManager.surveyStarted]
     */
    @PostMapping("/survey/started")
    fun surveyStarted(
            @RequestParam userId: Long,
            @RequestParam officeId: Long
    ) = queueManager.surveyStarted(queueManager.userById(userId), queueManager.officeById(officeId))

    /**
     * see [QueueManager.surveyFinished]
     */
    @PostMapping("/survey/finished")
    fun surveyFinished(@RequestParam officeId: Long) =
            queueManager.surveyFinished(queueManager.officeById(officeId))

    /**
     * see [QueueManager.officeIsReady]
     */
    @PostMapping("/office/ready")
    fun officeIsReady(@RequestParam officeId: Long) =
            queueManager.officeIsReady(queueManager.officeById(officeId))

    /**
     * see [QueueManager.officeIsBusy]
     */
    @PostMapping("/office/busy")
    fun officeIsBusy(@RequestParam officeId: Long) =
            queueManager.officeIsBusy(queueManager.officeById(officeId))

    /**
     * see [QueueManager.officeIsClosed]
     */
    @PostMapping("/office/closed")
    fun officeIsClosed(@RequestParam officeId: Long) =
            queueManager.officeIsClosed(queueManager.officeById(officeId))

    /**
     * see [QueueManager.addToOfficeQueue]
     */
    @PostMapping("/user/addToQueue")
    fun addToOfficeQueue(
            @RequestParam userId: Long
    ) = queueManager.addToOfficeQueue(queueManager.userById(userId))

    /**
     * see [QueueManager.deleteFromOfficeQueue]
     */
    @PostMapping("/user/remove")
    fun removeFromOfficeQueue(
            @RequestParam userId: Long
    ) = queueManager.deleteFromOfficeQueue(queueManager.userById(userId))

    /**
     * see [QueueManager.userLeftQueue]
     */
    @DeleteMapping("/user")
    fun userLeftQueue(
            @RequestParam userId: Long
    ) = queueManager.userLeftQueue(queueManager.userById(userId))

    /**
     * Просмотр какие обследования еще не пройдены по типу
     */
    @GetMapping("/surveys")
    fun notVisitedSurveys(@RequestParam surveyTypeId: Long) =
            queueManager.routeSheets.flatMap { it.surveys }.filter { it.surveyType.id == surveyTypeId && !it.visited}

    /**
     * see [QueueManager.deleteQueue]
     */
    @DeleteMapping
    fun deleteQueue() = queueManager.deleteQueue()

    /**
     * see [QueueManager.deleteHistory]
     */
    @DeleteMapping("/history")
    fun deleteHistory() = queueManager.deleteHistory()
}