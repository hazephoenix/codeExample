package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory
import ru.viscur.dh.datastorage.impl.repository.ObservationDefaultDurationRepository
import ru.viscur.dh.datastorage.impl.repository.ObservationDurationHistoryRepository
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import kotlin.math.roundToInt

/**
 * Created at 06.11.2019 12:04 by SherbakovaMA
 */
@Service
class ObservationDurationEstimationServiceImpl(
        private val durationHistoryRepository: ObservationDurationHistoryRepository,
        private val defaultDurationRepository: ObservationDefaultDurationRepository
) : ObservationDurationEstimationService {

    override fun deleteAllHistory() {
        durationHistoryRepository.deleteAll()
    }

    override fun saveToHistory(code: String, diagnosis: String, severity: Severity, duration: Int) {
        durationHistoryRepository.save(ObservationDurationHistory(
                code = code,
                diagnosis = diagnosis,
                severity = severity.name,
                duration = duration
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