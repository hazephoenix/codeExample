package ru.viscur.dh.mis.integration.impl.ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.*
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.Severity.*
import ru.viscur.dh.queue.api.QueueManagerService
import java.util.stream.Stream

/**
 * Created at 07.11.2019 12:35 by SherbakovaMA
 *
 * Тест для [QueueManagerService.recalcNextOffice]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class RecalcNextOfficeConfigTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    class TestCase(
            desc: String,
            queue: List<QueueOfOfficeSimple>,
            carePlan: CarePlanSimple,
            val recalcNextOffice: Boolean,
            val expOfficeId: String,
            val expOnum: Int
    ) : BaseTestCase(desc, queue, carePlan)

    companion object {
        private val testCases = listOf(
                TestCase(desc = "Настройка выключена. Должен пойти в последовательности как в маршрутном листе",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_130, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        )), QueueOfOfficeSimple(
                                OFFICE_202, listOf(
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
                        )),
                        recalcNextOffice = false,
                        expOfficeId = OFFICE_130, expOnum = 2
                ),
                TestCase(desc = "Настройка включена. Должен из непройденных выбрать лучший вариант",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_130, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        )), QueueOfOfficeSimple(
                                OFFICE_202, listOf(
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
                        )),
                        recalcNextOffice = true,
                        expOfficeId = OFFICE_202, expOnum = 0
                )
        )

        @JvmStatic
        private fun casesProvider(): Stream<Arguments> = Stream.of(*(testCases.map { Arguments.of(it) }.toTypedArray()))
    }

    @ParameterizedTest(name = "{index} => case={0}")
    @MethodSource("casesProvider")
    fun test(case: TestCase) {
        forTestService.cleanDb()
        val patientId = forTestService.prepareDb(case)

        queueManagerService.recalcNextOffice(case.recalcNextOffice)
        queueManagerService.addToQueue(patientId)

        val queueStr = forTestService.formQueueInfo()
        val queueItems = queueManagerService.queueItems()
        val foundQueueItem = queueItems.find { it.subject.id == patientId }
        assertNotNull(foundQueueItem, "${case.desc}. not found queueItem of patient with id '$patientId'. actual queue:\n$queueStr\n")
        foundQueueItem?.let {
            assertEquals(case.expOfficeId, it.location.id, "${case.desc}. wrong location of patientId '$patientId'. actual queue:\n$queueStr\n")
            assertEquals(case.expOnum, it.onum, "${case.desc}. wrong onum of patientId '$patientId'. actual queue:\n$queueStr\n")
        }
    }
}