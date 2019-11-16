package ru.viscur.dh.apps.paramedicdevice.device

import org.apache.batik.transcoder.TranscoderInput
import org.apache.batik.transcoder.TranscoderOutput
import org.apache.batik.transcoder.image.PNGTranscoder
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.annotation.Profile
import org.springframework.context.event.EventListener
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.ResourceLoader
import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.*
import ru.viscur.dh.apps.paramedicdevice.enums.TonometerErrorCode
import ru.viscur.dh.common.dto.events.TaskComplete
import ru.viscur.dh.common.dto.events.TaskRequested
import ru.viscur.dh.common.dto.events.TaskStarted
import ru.viscur.dh.common.dto.task.TaskType
import java.io.ByteArrayOutputStream
import java.lang.Thread.sleep
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.*
import javax.annotation.PostConstruct

/**
 * Заглушка для устройств
 */
@Component
@Profile("fake-device")
class FakeDevice(private val publisher: ApplicationEventPublisher, private val resourceLoader: ResourceLoader) {

    private val png: MutableList<String> = mutableListOf()

    @PostConstruct
    fun postConstruct() {
        arrayOf("ecg1_0.svg", "ecg2_0.svg", "ecg3_0.svg").forEach { svg ->
            val r = ClassPathResource(svg)
            val uri = r.url.toString()
            val input = TranscoderInput(uri)
            ByteArrayOutputStream().use {baos ->
                val output = TranscoderOutput(baos)
                val converter = PNGTranscoder()
                converter.transcode(input, output)
                baos.flush()
                png.add(Base64.getEncoder().encodeToString(baos.toByteArray()))
            }
        }
    }

    @EventListener(TaskRequested::class)
    fun eventListener(event: TaskRequested) {
        val task = event.task
        publisher.publishEvent(TaskStarted(task))
        task.result = when (task.type) {
            TaskType.Document -> {
                sleep(3_000L)
                DocumentResponse(
                        firstName = "Евгений",
                        middleName = "Александрович",
                        lastName = "Тимочкин",
                        birthDate = "31.03.1985",
                        fullName = "Тимочкин Евгений Александрович"
                )
            }
            TaskType.Height -> TvesResponse(value = "104.3", unit = "кг", code = "OK", message = "Выполнено успешно")
            TaskType.Weight -> TvesResponse(value = "181", unit = "см", code = "measuring", message = "Измерение")
            TaskType.Temperature -> {
                sleep(3000L)
                TemperatureResponse(37.1, "celsius")
            }
            TaskType.Tonometer -> {
                sleep(45_000L)
                TonometerResponse(
                        dateTime = Timestamp.valueOf(LocalDateTime.now()),
                        systolicBP = 80,
                        diastolicBP = 117,
                        meanArterialBP = 100,
                        pulseRate = 75,
                        pressurizationSetupValue = 0,
                        maxPulseAmplitude = 0,
                        tonometerModel = "Tonometer2000",
                        error = TonometerError(TonometerErrorCode.E00, "OK")
                )
            }
            TaskType.Electrocardiograph -> {
                sleep(25_000L)
                ElectrocardiographResponse(
                        heartRate = 75,
                        ecg1 = png[0],
                        ecg2 = png[1],
                        ecg3 = png[2],
                        rsp = 20
                )
            }
            else -> null
        }

        publisher.publishEvent(TaskComplete(task))
    }

}