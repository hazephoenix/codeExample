package ru.viscur.dh.integration.mis.api.paramedic

import java.util.*
import kotlin.Exception

/**
 * Created at 15.10.2019 13:45 by TimochkinEA
 *
 * @property id             ID команды для дальнейшей идентификации (UUID)
 * @property workstationId  ID АРМ Фельдшера, на котором надо выполнить команду
 * @property status         статус исполнения команды
 * @property error          Ошибка при выполнении команды
 * @property result         Результат выполнения команды
 */
data class WorkstationCmd (
        val id: String = UUID.randomUUID().toString(),
        val workstationId: String,
        var status: WorkstationCmdStatus = WorkstationCmdStatus.READY,
        val job: WorkstationJob,
        var error: Exception? = null,
        var result: Any? = null
)
