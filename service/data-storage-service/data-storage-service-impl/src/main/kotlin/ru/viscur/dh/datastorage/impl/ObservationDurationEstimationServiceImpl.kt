package ru.viscur.dh.datastorage.impl

import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory
import ru.viscur.dh.datastorage.impl.repository.ObservationDefaultDurationRepository
import ru.viscur.dh.datastorage.impl.repository.ObservationDurationHistoryRepository
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.fhir.model.utils.msToSeconds
import ru.viscur.dh.fhir.model.utils.toTimestamp
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import java.util.*
import kotlin.math.roundToInt

/**
 * Created at 06.11.2019 12:04 by SherbakovaMA
 */
@Service
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory", transactionManagerRef = "dsTxManager")
class ObservationDurationEstimationServiceImpl(
        private val durationHistoryRepository: ObservationDurationHistoryRepository,
        private val defaultDurationRepository: ObservationDefaultDurationRepository
) : ObservationDurationEstimationService {

    @Tx
    override fun deleteAllHistory() {
        durationHistoryRepository.deleteAll()
    }

    @Tx
    override fun saveToHistory(patientId: String, code: String, diagnosis: String, severity: Severity, start: Date, end: Date) {
        durationHistoryRepository.save(ObservationDurationHistory(
                patientId = patientId,
                code = code,
                fireDate = start.toTimestamp(),
                diagnosis = diagnosis,
                severity = severity.name,
                duration = msToSeconds(end.time - start.time)
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
}