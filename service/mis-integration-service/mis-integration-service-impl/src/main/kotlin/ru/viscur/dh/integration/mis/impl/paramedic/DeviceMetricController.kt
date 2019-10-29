package ru.viscur.dh.integration.mis.impl.paramedic

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.integration.mis.api.paramedic.WorkstationCmdStatus
import ru.viscur.dh.integration.mis.api.paramedic.WorkstationJob

/**
 * Created at 15.10.2019 12:21 by TimochkinEA
 */
@RestController
@RequestMapping("workstation/command")
class DeviceMetricController(private val manager: WorkstationCommandManager) {

    @GetMapping("execute/{workstationId}/{job}")
    fun executeCommand(@PathVariable workstationId: String, @PathVariable job: WorkstationJob): String {
        return manager.runJob(workstationId, job)
    }

    @GetMapping("status/{commandId}")
    fun status(@PathVariable commandId: String): WorkstationCmdStatus = manager.jobStatus(commandId)
}
