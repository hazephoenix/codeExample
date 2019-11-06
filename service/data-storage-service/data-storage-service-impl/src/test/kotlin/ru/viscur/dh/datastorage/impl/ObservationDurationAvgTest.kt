package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.impl.config.DataStorageConfig
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import java.util.stream.Stream

/**
 * Created at 06.11.2019 12:31 by SherbakovaMA
 *
 * Тест для [ObservationDurationEstimationService.avgByHistory]
 */
@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only")
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory", transactionManagerRef = "dsTxManager")
class ObservationDurationAvgTest() {

    @Autowired
    lateinit var service: ObservationDurationEstimationService

    /**
     * @param historyRecords значения которые должны быть в базе на момент проверки
     * @param searchingParams параметры поиска определения среднего значения продолжительности
     * @param exp ожидаемый результат
     */
    data class TestCase(
            val desc: String,
            val historyRecords: List<ObservationDurationHistory>,
            val searchingParams: ObservationDurationHistory,
            val exp: Int?
    )

    companion object {

        private const val code1 = "СтХир"
        private const val code2 = "СтТер"
        private const val diagnosis1 = "A00.1"
        private const val diagnosis2 = "A00.2"
        private val severity1 = Severity.RED.name
        private val severity2 = Severity.GREEN.name

        private val testCases = listOf(
                TestCase(desc = "Простой поиск по полному совпадению code+diagnosis+severity", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 600),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis2, severity = severity2, duration = 1000),
                        ObservationDurationHistory(code = code2, diagnosis = diagnosis2, severity = severity2, duration = 1000)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 600),
                TestCase(desc = "Поиск того чего нет", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 600)
                ),
                        searchingParams = ObservationDurationHistory(code = code2, diagnosis = diagnosis2, severity = severity2),
                        exp = null),
                TestCase(desc = "Поиск по совпадению code+diagnosis при отсутсвия лучшего совпадения", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity2, duration = 10),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity2, duration = 20),
                        ObservationDurationHistory(code = "other", diagnosis = "other", severity = severity2, duration = 1000)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 15),
                TestCase(desc = "Поиск по совпадению code+severity при отсутсвия лучшего совпадения", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis2, severity = severity1, duration = 10),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis2, severity = severity1, duration = 20),
                        ObservationDurationHistory(code = "other", diagnosis = "other", severity = severity2, duration = 1000)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 15),
                TestCase(desc = "Поиск по совпадению code при отсутсвия лучшего совпадения", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis2, severity = severity2, duration = 10),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis2, severity = severity2, duration = 20),
                        ObservationDurationHistory(code = "other", diagnosis = "other", severity = severity2, duration = 1000)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 15),
                TestCase(desc = "Среднее. Округление в меньшую сторону", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 602),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 602),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 603)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 602),
                TestCase(desc = "Среднее. Округление в большую сторону", historyRecords = listOf(
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 602),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 603),
                        ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1, duration = 603)
                ),
                        searchingParams = ObservationDurationHistory(code = code1, diagnosis = diagnosis1, severity = severity1),
                        exp = 603)
        )

        @JvmStatic
        private fun casesProvider(): Stream<Arguments> = Stream.of(*(testCases.map { Arguments.of(it) }.toTypedArray()))
    }

    @ParameterizedTest(name = "{index} => case={0}")
    @MethodSource("casesProvider")
    fun test(case: TestCase) {
        service.deleteAllHistory()
        case.historyRecords.forEach {
            service.saveToHistory(it.code!!, it.diagnosis!!, enumValueOf(it.severity!!), it.duration!!)
        }
        assertEquals(case.exp,
                service.avgByHistory(case.searchingParams.code!!, case.searchingParams.diagnosis!!, enumValueOf(case.searchingParams.severity!!)),
                "${case.desc}. wrong calculating avg for ${case.searchingParams}")
        service.deleteAllHistory()
    }

    @Test
    fun testEstimateDefaultValueNotFound() {
        assertEquals(10 * SECONDS_IN_MINUTE, service.estimate("12", "1", enumValueOf(severity1)),
                "wrong default observation duration")
    }
}