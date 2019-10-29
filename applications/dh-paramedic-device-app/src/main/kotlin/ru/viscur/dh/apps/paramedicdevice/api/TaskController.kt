package ru.viscur.dh.apps.paramedicdevice.api

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.service.TaskDispatcher

/**
 * Created at 28.10.2019 11:39 by TimochkinEA
 *
 * Контроллер для обработки задач, выполняемых на АРМ
 */
@RestController()
@RequestMapping("api/task")
class TaskController(private val dispatcher: TaskDispatcher) {

    /**
     * Запрос на выполнение задачи
     */
    @PostMapping("add", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun add(@RequestBody task: Task): Task {
        dispatcher.add(task)
        return task
    }

    /**
     * Текущий статус задачи
     * @param id    id задачи, присвоенный при отправке на выполнение
     */
    @GetMapping("status/{id}")
    fun status(@PathVariable id: String) = dispatcher.taskStatus(id)

    @GetMapping("result/{id}")
    fun result(@PathVariable id: String) = dispatcher.taskResult(id)
}
