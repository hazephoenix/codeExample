package ru.viscur.dh.datastorage.impl

import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory
import ru.viscur.dh.datastorage.impl.repository.ObservationDefaultDurationRepository
import ru.viscur.dh.datastorage.impl.repository.ObservationDurationHistoryRepository
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import ru.viscur.dh.fhir.model.utils.nowAsTimeStamp
import ru.viscur.dh.transaction.desc.config.annotation.Tx
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
    override fun saveToHistory(code: String, diagnosis: String, severity: Severity, duration: Int) {
        durationHistoryRepository.save(ObservationDurationHistory(
                code = code,
                fireDate = nowAsTimeStamp(),
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