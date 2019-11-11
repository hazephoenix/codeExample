package ru.viscur.dh.integration.mis.rest.validator

import org.springframework.validation.Errors
import org.springframework.validation.ValidationUtils
import org.springframework.validation.Validator
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType

/**
 * Created at 09.11.2019 14:33 by TimochkinEA
 *
 * Валидатор для запросов на выполнение задач
 */

class TaskRequestValidator: Validator {
    override fun validate(target: Any, errors: Errors) {
        target as Task
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "desktopId", "", "desktopId is required!")
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "", "typeId is required!")
        if (TaskType.Unknown == target.type) {
            errors.rejectValue("type", "", "type must be one of ${TaskType.values().filter{ it != TaskType.Unknown }.joinToString(",")}")
        }
    }

    override fun supports(clazz: Class<*>): Boolean = clazz.isAssignableFrom(Task::class.java)
}
