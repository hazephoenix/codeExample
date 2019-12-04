package ru.viscur.dh.datastorage.impl

import org.slf4j.LoggerFactory
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ConfigService
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.api.util.CLINICAL_IMPRESSION
import ru.viscur.dh.datastorage.impl.entity.ObservationDefaultDuration
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory
import ru.viscur.dh.datastorage.impl.repository.ObservationDefaultDurationRepository
import ru.viscur.dh.datastorage.impl.repository.ObservationDurationHistoryRepository
import ru.viscur.dh.fhir.model.dto.ObservationDuration
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import java.util.*
import kotlin.math.roundToInt

/**
 * Created at 06.11.2019 12:04 by SherbakovaMA
 */
@Service
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory", transactionManagerRef = "dsTxManager")
class ObservationDurationEstimationServiceImpl(
        private val configService: ConfigService,
        private val durationHistoryRepository: ObservationDurationHistoryRepository,
        private val defaultDurationRepository: ObservationDefaultDurationRepository
) : ObservationDurationEstimationService {

    private val log = LoggerFactory.getLogger(ObservationDurationEstimationServiceImpl::class.java)

    override fun recentObservationsByPatientId(patientId: String): List<ObservationDuration> {
        //за последние сутки
        val periodEnd = now()
        val periodStart = periodEnd.plusDays(-1)
        return durationHistoryRepository.findAllByPatientIdIsAndFireDateAfterAndFireDateBeforeOrderByFireDateAscIdAsc(
                patientId,
                periodStart.toTimestamp(),
                periodEnd.toTimestamp()
        ).map { ObservationDuration(it.patientId!!, it.fireDate!!.toDate(), it.code!!, it.duration!!) }
    }

    @Tx
    override fun deleteAllHistory() {
        durationHistoryRepository.deleteAll()
    }

    @Tx
    override fun saveToHistory(patientId: String, code: String, diagnosis: String?, severity: Severity, start: Date, end: Date) {
        durationHistoryRepository.save(ObservationDurationHistory(
                patientId = patientId,
                code = code,
                fireDate = start.toTimestamp(),
                diagnosis = diagnosis,
                severity = severity.name,
                duration = durationInSeconds(start, end)
        ))
    }

    override fun avgByHistory(code: String, diagnosis: String, severity: Severity): Int? = (
            durationHistoryRepository.avgByAll(code, diagnosis, severity.name)
                    ?: durationHistoryRepository.avgByCodeAndDiagnosis(code, diagnosis)
                    ?: durationHistoryRepository.avgByCodeAndSeverity(code, severity.name)
                    ?: durationHistoryRepository.avgByCode(code)
            )?.roundToInt()

    override fun estimate(code: String, diagnosis: String, severity: Severity): Int {
        return (avgByHistory(code, diagnosis, severity)
                ?: defaultDurationRepository.findFirstByCodeIsAndDiagnosisIsAndSeverityIs(code, diagnosis, severity.name)?.duration)
                ?: 10 * SECONDS_IN_MINUTE
    }

    override fun avgByHistoryStrictSearch(code: String, severity: Severity) =
            durationHistoryRepository.avgByCodeAndSeverity(code, severity.name)?.roundToInt()

    override fun defaultDuration(code: String, severity: Severity): Int {
        return defaultDurationRepository.findFirstByCodeIsAndSeverityIs(code, severity.name)?.duration
                ?: throw Exception("not found ObservationDefaultDuration with code '$code' and severity '$severity'")
    }

    override fun severitiesToDefaultDuration(code: String): Map<String, Int> = Severity.values().associate { it.name to defaultDuration(code, it) }

    @Tx
    override fun updateDefaultDuration(code: String, severity: Severity, duration: Int) {
        val item = (defaultDurationRepository.findFirstByCodeIsAndSeverityIs(code, severity.name)
                ?: ObservationDefaultDuration(code = code, severity = severity.name)).apply {
            this.duration = duration
        }
        defaultDurationRepository.save(item)
    }

    @Tx
    override fun recalcDefaultClinicalImpressionDurations() {
        Severity.values().forEach { severity ->
            val configCode = severity.configAutoCorrectionDuration
            val needRecalc = configService.readBool(configCode)
            if (needRecalc) {
                val duration = avgByHistoryStrictSearch(CLINICAL_IMPRESSION, severity)
                duration?.run {
                    updateDefaultDuration(CLINICAL_IMPRESSION, severity, duration)
                } ?: run {
                    log.error("Not found duration in history for severity '$severity' and observation 'CLINICAL_IMPRESSION'. " +
                            "Skipping updating default duration")
                }
            }
        }
    }
}