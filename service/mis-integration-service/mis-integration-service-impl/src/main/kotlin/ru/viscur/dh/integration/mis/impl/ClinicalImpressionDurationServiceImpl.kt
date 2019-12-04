package ru.viscur.dh.integration.mis.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ClinicalImpressionService
import ru.viscur.dh.datastorage.api.ConfigService
import ru.viscur.dh.datastorage.api.ObservationDurationEstimationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.util.CLINICAL_IMPRESSION
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.durationInSeconds
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.integration.mis.api.ClinicalImpressionDurationService
import ru.viscur.dh.integration.mis.api.dto.ClinicalImpressionDeafualtDurationDto
import ru.viscur.dh.integration.mis.api.dto.ClinicalImpressionDurationDto
import ru.viscur.dh.transaction.desc.config.annotation.Tx

/**
 * Created at 12.11.2019 16:07 by SherbakovaMA
 */
@Service
class ClinicalImpressionDurationServiceImpl(
        private val configService: ConfigService,
        private val patientService: PatientService,
        private val observationDurationService: ObservationDurationEstimationService,
        private val clinicalImpressionService: ClinicalImpressionService

) : ClinicalImpressionDurationService {

    @Tx
    override fun updateDefaultDuration(severity: Severity, duration: Int) {
        observationDurationService.updateDefaultDuration(CLINICAL_IMPRESSION, severity, duration)
    }

    @Tx
    override fun updateConfigAutoRecalc(severity: Severity, value: Boolean) {
        configService.write(severity.configAutoCorrectionDuration, value.toString())
    }

    override fun currentDurations(): List<ClinicalImpressionDurationDto> {
        val severityToDefaultDuration = observationDurationService.severitiesToDefaultDuration(CLINICAL_IMPRESSION)
        val allActive = clinicalImpressionService.allActive()
        val now = now()
        return allActive.map {
            val patientId = it.subject.id!!
            val severity = patientService.severity(patientId)
            ClinicalImpressionDurationDto(
                    patientId = patientId,
                    start = it.date,
                    duration = durationInSeconds(it.date, now),
                    defaultDuration = severityToDefaultDuration[severity.name]
                            ?: throw Exception("not found default duration for severity '${severity.name}'")
            )
        }
    }

    override fun defaultDurations(): List<ClinicalImpressionDeafualtDurationDto> =
            Severity.values().map {
                ClinicalImpressionDeafualtDurationDto(
                        severity = it.name,
                        severityDisplay = it.display,
                        defaultDuration = observationDurationService.defaultDuration(CLINICAL_IMPRESSION, it),
                        autoRecalc = configService.readBool(it.configAutoCorrectionDuration)
                )
            }

}