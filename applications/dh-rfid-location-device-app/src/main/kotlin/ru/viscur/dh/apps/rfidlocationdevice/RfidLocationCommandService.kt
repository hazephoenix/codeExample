package ru.viscur.dh.apps.rfidlocationdevice

import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Service
import ru.viscur.dh.location.api.LOCATION_EVENT_READER_CAUGHT_TAGS
import ru.viscur.dh.location.api.ReaderEvent
import java.time.Instant

/**
 * Сервис входная точка для выполнения команд приложения
 */
@Service
class RfidLocationCommandService(
        private val jmsTemplate: JmsTemplate
) {
    /**
     * Определение и выполенение команд по набору переданных аргументов
     */
    fun execute(appId: String?, replay: Boolean, generate: String?, output: String?, files: Array<String>?) {
        when {
            appId != null -> {
                val logFile = output ?: "default.out"
                check(!replay)
                check(generate == null)
                check(files.isNullOrEmpty())

                println("Start in run mode, save replay log to '$output'")
                start(logFile)
            }
            replay -> {
                check(output == null)
                check(generate == null)
                check(!files.isNullOrEmpty())

                println("Replay mode, replay events from ${files.joinToString(",", "'", "'")}")
                replay(files)
            }
            generate != null -> {
                val logFile = output ?: "$generate.log"
                check(files.isNullOrEmpty())
                "generate"
                generate(generate, logFile)
            }
            else -> throw IllegalStateException("One of --app-id, --replay, --generate] option required ")
        }
    }

    private fun readEventsFromFiles(files: Array<String>): List<ReaderEvent> {
        val readers = files.map { LogFileReader(it) }
        val list = mutableListOf<ReaderEvent>()

        while (true) {
            val c = readers.filter { it.current != null }.minBy { it.current!! }
            c?.current?.let {
                try {
                    list += parseLine(it)
                } catch (e: Throwable) {
                    println("Error on parsing line '$it' from file ${c.fileName}: ${e.message}")
                }
                c.next()
            } ?: break
        }

        return list
    }

    private fun replay(files: Array<String>, replaceDate: Boolean = true) {
        val events = readEventsFromFiles(files)
        println("${events.size} events collected")
        var last: Long = 0
        for (event in events) {
            if (last != 0L) {
                Thread.sleep(event.stamp.toEpochMilli() - last)
            }
            val actualEvent = event.copy(stamp = Instant.now())
            println(actualEvent)
            jmsTemplate.convertAndSend(LOCATION_EVENT_READER_CAUGHT_TAGS, actualEvent)
            last = event.stamp.toEpochMilli()
        }
    }

    private fun start(logFile: String): Nothing {
        TODO("not implemented")
    }

    private fun generate(generate: String, logFile: String): Nothing {
        TODO("not implemented")
    }
}
