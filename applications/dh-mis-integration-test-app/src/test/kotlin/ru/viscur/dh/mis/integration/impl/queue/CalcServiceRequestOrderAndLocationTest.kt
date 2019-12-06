package ru.viscur.dh.mis.integration.impl.queue

import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.queue.api.QueueManagerService
import java.util.stream.Stream

/**
 * Created at 19.11.2019 11:53 by SherbakovaMA
 *
 * Тест на определение порядка и кабинетов у назначений в маршрутном листе
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class CalcServiceRequestOrderAndLocationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var receptionService: ReceptionService

    @Autowired
    lateinit var forTestService: ForTestService

    class TestCase(
            desc: String,
            queue: List<QueueOfOfficeSimple>,
            carePlan: CarePlanSimple,
            val recalcNextOffice: Boolean,
            val expServReqs: List<ServiceRequestSimple>
    ) : BaseTestCase(desc, queue, carePlan)

    companion object {
        private val testCases = listOf(
                TestCase(desc = "Пересчет включен. Выбор простой - есть мин время*дальность",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_202, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120)
                        )),
                        recalcNextOffice = true,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет включен. Несколько мин время*дальность - смотрится по времени",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 6 * SECONDS_IN_MINUTE)//от 101 0.5
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)//от 101 0,6
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 20 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        recalcNextOffice = true,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_116),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет включен. Несколько мин время*дальность и нексколько по времени - смотрится по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        recalcNextOffice = true,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_116),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет включен. Несколько мин время*дальность и нексколько по времени - смотрится по общей очереди (регистрируется красный)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.RED, estDuration = 5 * SECONDS_IN_MINUTE),
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.GREEN, estDuration = 5 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.RED, estDuration = 5 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(severity = Severity.RED, servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        recalcNextOffice = true,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_117),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Простое определение по мин. времени ожидания",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_202, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Несколько с мин. временем ожидания, смотрим по дальности",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.9
                        )), QueueOfOfficeSimple(OFFICE_202, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.8
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Несколько с мин. временем ожидания, неск. по дальности, смотрим по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.9
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.6
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.6
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_116),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Несколько с мин. временем ожидания, неск. по дальности, смотрим по общей очереди (ставим красного)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.RED, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.9
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.RED, estDuration = 10 * SECONDS_IN_MINUTE),//от 101 0.6
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.GREEN, estDuration = 10 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, severity = Severity.RED, estDuration = 10 * SECONDS_IN_MINUTE)//от 101 0.6
                        ))),
                        carePlan = CarePlanSimple(severity = Severity.RED, servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго - в том же секторе (по мин. времени, но у нас нет сектора с 3мя кабинетами - нельзя проверить)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)//группа 7
                        )), QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)//группа 4
                        )), QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)//группа 4
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго/последующих: в том же секторе нет - смотрим по дальности (от последнего кабинета)," +
                        "от 150 до 120 - 0.4. направляется в него после 150, хоть и в 116/117 почти нет очереди (там дальность 1 - больше)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 20 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 120 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго/последующих: в том же секторе нет - смотрим по дальности (от последнего кабинета)," +
                        "от 150 до 116 - 0.6. направляется в него после 150, хоть и в 202 почти нет очереди (там дальность 0.8 - больше)" +
                        "аналог предыдущего, проверка на несколько услуг в кабинете",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 20 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_202, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_150)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго/последующих: в том же секторе нет, неск по дальности, смотрим по времени",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 20 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_117),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго/последующих: в том же секторе нет, неск по дальности, смотрим по времени. (Аналог предыдущего, но нет 150 - в том же секторе)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 20 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_117),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Определение второго/последующих: в том же секторе нет, неск по дальности, неск по времени, смотрим по алфавиту",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_116),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Полная проверка. Сначала по времени - 120, потом по дальности: от 120: 149/150 - 0,4, 116/117 - 1, 202 - 1,1. " +
                        "Выбирается 149/150 - из них 150 по мин времени, затем 149 т к в том же секторе. Потом по дальности от 149: 116/117 - 0,6, 202 - 0,8. Значит, 116, потом 202",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_120, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 1 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_149, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 50 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_116, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 10 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_117, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 60 * SECONDS_IN_MINUTE)
                        )), QueueOfOfficeSimple(OFFICE_202, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 5 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_116_AND_117),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_120)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_120),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_149),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_116_AND_117, locationId = OFFICE_116),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                ),
                TestCase(desc = "Пересчет выключен. Проверка, что 151 (ЭГДС) имеет самый низкий приоритет (перед осмотром отв.)",
                        queue = listOf(QueueOfOfficeSimple(OFFICE_150, listOf(
                                QueueItemSimple(status = PatientQueueStatus.IN_QUEUE, estDuration = 40 * SECONDS_IN_MINUTE)
                        ))),
                        carePlan = CarePlanSimple(servReqs = listOf(
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_151),
                                ServiceRequestSimple(OBSERVATION_IN_OFFICE_150)
                        )),
                        recalcNextOffice = false,
                        expServReqs = listOf(
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_104),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_150),
                                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_151),
                                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON)
                        )
                )
        )

        @JvmStatic
        private fun casesProvider(): Stream<Arguments> = Stream.of(*(testCases.map { Arguments.of(it) }.toTypedArray()))
    }

    @ParameterizedTest(name = "{index} => case={0}")
    @MethodSource("casesProvider")
    fun test(case: TestCase) {
        forTestService.cleanDb()
        forTestService.prepareQueue(case)
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена

        queueManagerService.recalcNextOffice(case.recalcNextOffice)
        val receivedServReqs = forTestService.registerPatient(case.carePlan.servReqs)
        val patientId = receivedServReqs.first().subject!!.id()
        forTestService.checkServiceRequestsOfPatient(patientId, case.expServReqs, case.desc)
    }
}