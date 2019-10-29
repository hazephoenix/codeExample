package ru.viscur.dh.integration.mis.impl.paramedic

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.viscur.dh.integration.mis.api.paramedic.WorkstationCmd
import ru.viscur.dh.integration.mis.api.paramedic.WorkstationCmdStatus
import ru.viscur.dh.integration.mis.api.paramedic.WorkstationJob
import java.util.concurrent.Executors

/**
 * Created at 15.10.2019 17:41 by TimochkinEA
 */
@Service
class WorkstationCommandManager {

    private val executionPool: MutableMap<String, WorkstationCmd> = mutableMapOf()

    private val executor = Executors.newFixedThreadPool(3)

    @Autowired
    private lateinit var jmsTemplate: JmsTemplate

    fun runJob(workstationId: String, job: WorkstationJob): String {
        val cmd = WorkstationCmd(workstationId = workstationId, job = job)
        executor.submit {
            try {
                cmd.status = WorkstationCmdStatus.RUNNING
                jmsTemplate.convertAndSend("$workstationId-commands", cmd)
                executionPool[cmd.id] = cmd
                cmd.result = jmsTemplate.receive("$workstationId-results")
                cmd.status = WorkstationCmdStatus.SUCCESS
            } catch (e: Exception) {
                cmd.status = WorkstationCmdStatus.ERROR
                cmd.error = e
            }
        }
        return cmd.id
    }

    fun jobStatus(id: String): WorkstationCmdStatus = executionPool[id]?.status ?: WorkstationCmdStatus.NOT_EXISTS

    @Scheduled(fixedRate = 30000L)
    private fun removeSuccessCommands() {
        val done = executionPool.filter { (_, value) -> value.status == WorkstationCmdStatus.SUCCESS }
        done.forEach {(key, _) -> executionPool.remove(key)}
    }
}
