package ru.viscur.dh.apps.paramedicdevice.device

import com.zebra.sdk.comm.Connection
import com.zebra.sdk.printer.discovery.UsbDiscoverer
import com.zebra.sdk.printer.discovery.ZebraPrinterFilter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType
import javax.annotation.PostConstruct

/**
 * Created at 05.11.2019 11:40 by TimochkinEA
 *
 * Печать браслетов
 */
@Component
@Profile("!fake-device")
class WristbandPrinter(
        private val publisher: ApplicationEventPublisher,
        @Value("\${wristband.red:RED}")
        private val redKey: String,
        @Value("\${wristband.yellow:YELLOW}")
        private val yellowKey: String,
        @Value("\${wristband.green:GREEN}")
        private val greenKey: String
) {

    private val log: Logger = LoggerFactory.getLogger(WristbandPrinter::class.java)

    private val connections: MutableMap<String, Connection> = mutableMapOf()

    @PostConstruct
    private fun init() {
        log.debug("OS NAME: ${System.getProperty("os.name")}")
        val os = System.getProperty("os.name").toLowerCase()
        if (os.contains("windows")) {
            fillWinConnections()
        } else {
            fillLinuxConnections()
        }
    }

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Wristband) {
            if (log.isDebugEnabled) {
                log.debug("Take request to print wristband!")
                log.debug("$task")
            }
            publisher.publishEvent(TaskStarted(task))
            printWristband(task)
        }
    }

    private fun printWristband(task: Task) {
        val patientId = task.payload!!["patientId"] as String
        val queueCode = task.payload!!["queueCode"] as String
        try {
            val connect = when(queueCode[0]) {
                'К' -> connections[redKey]
                'Ж' -> connections[yellowKey]
                'З' -> connections[greenKey]
                else -> null
            }

            if (connect == null) {
                log.error("Unsupported queue code: $queueCode")
                publisher.publishEvent(TaskError(task))
            } else {

                val zpl = """
                    ^XA
                    ^MMT
                    ^PW203
                    ^LL2233
                    ^LS0
                    ^BY6,10^FT17,324^B7R,10,2,,,N
                    ^FH\^FD$patientId^FS
                    ^FT106,1436^A@R,78,78,TT0003M_^FH\^CI28^FD$queueCode^FS^CI0
                    ^PQ1,0,1,Y
                    ^XZ
                    """.trimIndent()
                connect.open()
                connect.write(zpl.toByteArray())
                connect.close()
                publisher.publishEvent(TaskComplete(task))
            }
        } catch (e: Exception) {
            log.error("Unable to print wristband!: ${e.message}", e)
            publisher.publishEvent(TaskError(task))
        }
    }

    /**
     * При работе из под windows нет возможности ориентироваться
     * на серийный номер устройства, можно завязаться на имя принтера
     */
    private fun fillWinConnections() {
        try {
            val printers = UsbDiscoverer.getZebraDriverPrinters()
            if (printers.isEmpty()) {
                log.warn("No Zebra Printers")
            } else {
                printers.forEach {
                    if (log.isDebugEnabled) {
                        log.debug(it.discoveryDataMap.toString())
                        log.debug(it.address)
                    }
                    connections[it.address] = it.connection
                }
            }
        } catch (e: Exception) {
            log.error("Fail to init wristband printers! : ${e.message}", e)
        }

    }

    private fun fillLinuxConnections() {
        try {
            val printers = UsbDiscoverer.getZebraUsbPrinters(ZebraPrinterFilter())
            if (printers.isEmpty()) {
                log.warn("No Zebra Printers")
            } else {
                printers.forEach {
                    if (log.isDebugEnabled) {
                        log.debug(it.discoveryDataMap.toString())
                        log.debug(it.address)
                    }
                    connections[it.discoveryDataMap["SERIAL_NUMBER"]!!] = it.connection
                }
            }
        } catch (e: Exception) {
            log.error("Fail to init wristband printers! : ${e.message}", e)
        }
    }
}
