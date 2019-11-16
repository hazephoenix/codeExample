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
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.enums.Severity.*
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.queue.api.QueueManagerService
import java.util.stream.Stream

/**
 * Created at 07.11.2019 12:35 by SherbakovaMA
 *
 * Тест для [QueueManagerService.addToQueue]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class AddToQueueTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var forTestService: ForTestService

    class TestCase(
            desc: String,
            queue: List<QueueOfOfficeSimple>,
            carePlan: CarePlanSimple,
            val expOfficeId: String,
            val expOnum: Int
    ) : BaseTestCase(desc, queue, carePlan)

    companion object {
        private val testCases = listOf(
                TestCase(desc = "1 блок. Порядок по степени тяжести. Зеленые в очереди. Ставим красного",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 0
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. Зеленые в очереди. Ставим желтого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 0
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. Зеленые в очереди. Ставим зеленого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 2
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди желтые и зеленые. Ставим красного",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 0
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди желтые и зеленые. Ставим желтого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 2
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди желтые и зеленые. Ставим зеленого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 4
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди красные, желтые и зеленые. Ставим красного",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 2
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди красные, желтые и зеленые. Ставим желтого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 4
                ),
                TestCase(desc = "1 блок. Порядок по степени тяжести. В очереди красные, желтые и зеленые. Ставим зеленого",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 6
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. Пустые очереди. Ставим красного. Ставится в первый по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 0
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. В одном зеленые. Ставим красного, учитывается общая продолжительность очереди, идет в свободный",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_117, expOnum = 0
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. В одном красные. Ставим красного, идет в свободный",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(RED, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_117, expOnum = 0
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. В одном красные. Ставим желтого, идет в свободный",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_117, expOnum = 0
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. Не учитываются пациенты которые уже идут на обследования (GOING_TO_OBSERVATION, ON_OBSERVATION). " +
                        "В одном есть GOING_TO_OBSERVATION. Ставим желтого, идет в первый по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.GOING_TO_OBSERVATION, RED)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 1
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. Не учитываются пациенты которые уже идут на обследования (GOING_TO_OBSERVATION, ON_OBSERVATION). " +
                        "В одном есть ON_OBSERVATION. Ставим желтого, идет в первый по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.GOING_TO_OBSERVATION, RED)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 1
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. Не учитываются пациенты которые уже идут на обследования (GOING_TO_OBSERVATION, ON_OBSERVATION). " +
                        "В одном есть ON_OBSERVATION, непустые очереди. Ставим желтого, идет в первый по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.GOING_TO_OBSERVATION, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 1
                ),
                TestCase(desc = "2 блок. Равнозначные кабинеты по приоритету и дальности. Не учитываются пациенты которые уже идут на обследования (GOING_TO_OBSERVATION, ON_OBSERVATION). " +
                        "Ставим желтого, идет в первый по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.GOING_TO_OBSERVATION, RED)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                                QueueItemSimple(PatientQueueStatus.ON_OBSERVATION, GREEN)
                        ))),
                        carePlan = CarePlanSimple(YELLOW, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 1
                ),
                TestCase(desc = "3 блок. Приоритетность назначений. При непустой очереди но приоритетном назначении идет в приоритетное назначение в 101",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_101, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, RED),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, YELLOW),
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        expOfficeId = OFFICE_101, expOnum = 3
                ),
                TestCase(desc = "4 блок. Разница ожидания более 15% (от минимального!), идет в кабинет с минимальным ожиданием",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 30 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 32 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_130, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 10 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_202, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 31 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
                        )),
                        expOfficeId = OFFICE_130, expOnum = 1
                ),
                TestCase(desc = "4 блок. Разница ожидания менее 15% (от минимального!). Берется в расчет что от RECEPTION ближе: 130, 116/117, 202. Выбирается 116, т к в уровень 15% попали 116/117 и 202",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_116, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 22 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_117, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 23 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_130, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 40 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(
                                OFFICE_202, listOf(
                                QueueItemSimple(PatientQueueStatus.IN_QUEUE, GREEN, 21 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
                        )),
                        expOfficeId = OFFICE_116, expOnum = 1
                ),
                TestCase(desc = "5 блок. Выполненные назначения не учитываются. Отправляется на невыполненное",
                        queue = listOf(QueueOfOfficeSimple(
                                OFFICE_130, listOf(
                        ))),
                        carePlan = CarePlanSimple(GREEN, listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117, status = ServiceRequestStatus.waiting_result),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_130)
                        )),
                        expOfficeId = OFFICE_130, expOnum = 0
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
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена

        queueManagerService.recalcNextOffice(true)
        queueManagerService.addToQueue(patientId)

        val queueItems = queueManagerService.queueItems()
        val foundQueueItem = queueItems.find { it.subject.id == patientId }
        val queueStr = forTestService.formQueueInfo()
        assertNotNull(foundQueueItem, "${case.desc}. not found queueItem of patient with id '$patientId'. actual queue:\n$queueStr\n")
        foundQueueItem?.let {
            assertEquals(case.expOfficeId, it.location.id, "${case.desc}. wrong location of patientId '$patientId'. actual queue:\n$queueStr\n")
            assertEquals(case.expOnum, it.onum, "${case.desc}. wrong onum of patientId '$patientId'. actual queue:\n$queueStr\n")
        }
    }
}