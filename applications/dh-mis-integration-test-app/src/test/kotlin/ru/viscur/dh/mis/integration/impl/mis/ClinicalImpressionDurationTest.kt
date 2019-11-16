package ru.viscur.dh.mis.integration.impl.mis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.MILLISECONDS_IN_SECOND
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.integration.mis.api.ClinicalImpressionDurationService
import ru.viscur.dh.queue.api.QueueManagerService
import java.util.*

/**
 * Created at 12.11.2019 8:42 by SherbakovaMA
 *
 * Тест на учет времени продолжительности обращения [ClinicalImpressionDurationService]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class ClinicalImpressionDurationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var clinicalImpressionDurationService: ClinicalImpressionDurationService

    @Autowired
    lateinit var observationDurationService: ObservationDurationEstimationService

    @Autowired
    lateinit var forTestService: ForTestService

    @Test
    fun `auto recalc default duration with autoRecalc on`() {
        observationDurationService.deleteAllHistory()
        val now = now()
        val severity = Severity.RED
        observationDurationService.saveToHistory(
                patientId = "anyPatientId",
                code = CLINICAL_IMPRESSION,
                diagnosis = "any diagnosis",
                severity = severity,
                start = Date(now.time - minutesToSeconds(5) * MILLISECONDS_IN_SECOND),
                end = now
        )
        clinicalImpressionDurationService.updateDefaultDuration(severity, minutesToSeconds(30))
        //проверка до
        checkDefaultDuration(severity, minutesToSeconds(30))
        //проверяемые действия
        clinicalImpressionDurationService.updateConfigAutoRecalc(severity, true)
        observationDurationService.recalcDefaultClinicalImpressionDurations()
        //проверка после
        checkDefaultDuration(severity, minutesToSeconds(5))
    }

    @Test
    fun `auto recalc default duration with autoRecalc off`() {
        observationDurationService.deleteAllHistory()
        val now = now()
        val severity = Severity.RED
        observationDurationService.saveToHistory(
                patientId = "anyPatientId",
                code = CLINICAL_IMPRESSION,
                diagnosis = "any diagnosis",
                severity = severity,
                start = Date(now.time - minutesToSeconds(5) * MILLISECONDS_IN_SECOND),
                end = now
        )
        clinicalImpressionDurationService.updateDefaultDuration(severity, minutesToSeconds(30))
        //проверка до
        checkDefaultDuration(severity, minutesToSeconds(30))
        //проверяемые действия
        clinicalImpressionDurationService.updateConfigAutoRecalc(severity, false)
        observationDurationService.recalcDefaultClinicalImpressionDurations()
        //проверка после
        checkDefaultDuration(severity, minutesToSeconds(30))
    }

    private fun minutesToSeconds(value: Int) = value * SECONDS_IN_MINUTE

    private fun checkDefaultDuration(severity: Severity, expDuration: Int) {
        val actDuration = clinicalImpressionDurationService.defaultDurations().find { it.severity == severity.name }!!.defaultDuration
        assertEquals(expDuration, actDuration, "wrong default duration")
    }
}