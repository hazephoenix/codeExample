package ru.viscur.dh.integration.mis.rest

import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.integration.mis.api.paramedic.TaskDispatcher
import ru.viscur.dh.integration.mis.rest.validator.TaskRequestValidator

/**
 * Created at 28.10.2019 11:39 by TimochkinEA
 *
 * Контроллер для обработки задач, выполняемых на АРМ
 */
@RestController
@RequestMapping("desktop/task")
class TaskController(private val dispatcher: TaskDispatcher) {

    /**
     * Запрос на выполнение задачи
     */
    @PostMapping("add", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun add(@RequestBody @Validated task: Task): Task = dispatcher.add(task)

    /**
     * Текущий статус задачи
     * @param id    ID задачи, присвоенный при отправке на выполнение
     */
    @GetMapping("status/{id}")
    fun status(@PathVariable id: String) = dispatcher.status(id)

    /**
     * Результат выполнения задачи
     * @param id    ID задачи
     */
    @GetMapping("result/{id}")
    fun result(@PathVariable id: String) = dispatcher.result(id)

    @InitBinder
    private fun binder(binder: WebDataBinder) {
        binder.addValidators(TaskRequestValidator())
    }
}
