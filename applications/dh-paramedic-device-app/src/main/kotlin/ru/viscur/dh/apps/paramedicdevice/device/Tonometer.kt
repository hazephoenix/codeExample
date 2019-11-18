package ru.viscur.dh.apps.paramedicdevice.device

import com.fazecast.jSerialComm.SerialPort
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.TonometerError
import ru.viscur.dh.apps.paramedicdevice.dto.TonometerResponse
import ru.viscur.dh.apps.paramedicdevice.enums.TonometerErrorCode
import ru.viscur.dh.apps.paramedicdevice.utils.*
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskError
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.Task
import ru.viscur.dh.common.dto.task.TaskType
import java.sql.Timestamp
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Класс для работы с тонометром A&D TM-2655P
 *
 * Работает как под ОС Linux, так и под Windows (7+).
 * В ОС Linux требуются root-права доступа к порту
 *
 * Активный в случае, если нет профиля triton-monitor, если профиль включен, то заменяется [TritonTonometer]
 */
@Profile("!triton-monitor & !fake-device")
@Component
class Tonometer(
        @Value("\${paramedic.serial.port.system.name:0}")
        private val systemPortName: String,
        private val publisher: ApplicationEventPublisher
) {
    private val log: Logger = LoggerFactory.getLogger(Tonometer::class.java)

    private lateinit var comPort: SerialPort

    @PostConstruct
    fun postCreate() {
        comPort = SerialPort.getCommPort(systemPortName)
        comPort.openPort()
        comPort.baudRate = 2400
    }

    @PreDestroy
    fun preDestroy() {
        comPort.closePort()
    }

    @EventListener(TaskRequested::class)
    fun listener(event: TaskRequested) {
        val task = event.task
        if (task.type == TaskType.Tonometer) {
            publisher.publishEvent(TaskStarted(task))
            doMeasure(task)
        }
    }

    /**
     * Выполнить измерение
     */
    private fun doMeasure(task: Task) {
        try {
                log.info("Serial Port baud rate settings: ${comPort.baudRate}")
                log.info("Serial Port is opened: ${comPort.isOpen}")

                comPort.writeBytes(stopMeasuringCmd, stopMeasuringCmd.size.toLong())
                comPort.writeBytes(startMeasuringCmd, startMeasuringCmd.size.toLong())
                var measuring = true
                var resultArray = byteArrayOf()
                while (measuring) {
                    while (comPort.bytesAvailable() == 0)
                        Thread.sleep(20)

                    val readBuffer = ByteArray(comPort.bytesAvailable())
                    comPort.readBytes(readBuffer, readBuffer.size.toLong())
                    resultArray += readBuffer

                    if (readBuffer.find { it == ETX } != null) {
                        measuring = false
                        task.result = rbResult(resultArray)
                        publisher.publishEvent(TaskComplete(task))
                    }
                }
        } catch (e: Exception) {
            log.error("Error while take tonometer results!: ${e.message}", e)
            publisher.publishEvent(TaskError(task))
        }
    }

    /**
     * Считать даннные с измерения в RB-формате
     *
     * По умолчанию прибор настроен на измерение в RB-формате (F25:1).
     * RB-формат не содержит ID, содержит код ошибки и
     * среднее значение АД - MAP (mean arterial blood pressure)).
     *
     * см. документ с протоколом передачи данных
     */
    private fun rbResult(bytes: ByteArray): TonometerResponse? {
        val acknowledgment = bytes.copyOfRange(0, 6)
        val measuredData = bytes.copyOfRange(6, bytes.size)
        if (measuredData.size >= 63 && acknowledgment[acknowledgment.lastIndex] == ACK) {
            measuredData.find { it == STX }?.let {
                val start = measuredData.indexOf(it)
                return readIndicators(measuredData.copyOfRange(start, measuredData.size))
            }
        } else {
            throw Exception("Received data of wrong format")
        }
        return null
    }

    /**
     * Считать показатели прибора из полученного массива байтов
     */
    private fun readIndicators(bytes: ByteArray): TonometerResponse {
        val words = mutableListOf<CharArray>()
        var buffer = charArrayOf()
        for (i in 0..bytes.lastIndex) {
            if (bytes[i] == RS) {
                if (buffer.isNotEmpty()) {
                    words.add(buffer)
                    buffer = charArrayOf()
                }
            } else {
                if (bytes[i].toChar() != ' ')
                    buffer += bytes[i].toChar()
                if (i == bytes.lastIndex)
                    words.add(buffer)
            }
        }

        val errorCode = TonometerErrorCode.valueOf(dataString(words[4], 0))
        return TonometerResponse(
                tonometerModel = dataString(words[0], 1),
                dateTime = dataString(words[1], 0).let {
                    Timestamp.valueOf("20${it.substring(0, 2)}-${it.substring(2, 4)}-${it.substring(4, 6)} " +
                            "${it.substring(6, 8)}:${it.substring(8, 10)}:00")
                },
                error = TonometerError(code = errorCode, display = errorCode.display),
                systolicBP = dataString(words[5], 1).toInt(),
                meanArterialBP = dataString(words[6], 1).toInt(),
                diastolicBP = dataString(words[7], 1).toInt(),
                pulseRate = dataString(words[8], 1).toInt(),
                pressurizationSetupValue = dataString(words[9], 1).toInt(),
                maxPulseAmplitude = dataString(words[10], 1).toInt()
        )
    }

    private fun dataString(chars: CharArray, from: Int) =
            chars.copyOfRange(from, chars.size).joinToString("")
}
