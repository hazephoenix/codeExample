package ru.viscur.dh.datastorage.impl.utils

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.datastorage.impl.entity.*
import ru.viscur.dh.datastorage.impl.repository.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Класс для предположения диагноза в системе МКБ-10
 */
@Component
class DiagnosisPredictorImpl(
        private val resourceService: ResourceService,
        private val patientService: PatientService,
        private val clinicalImpressionService: ClinicalImpressionService,
        private val trainingSampleRepository: TrainingSampleRepository,
        private val codeMapService: CodeMapService,
        private val conceptService: ConceptService
): DiagnosisPredictor {
    /**
     * Минимальная вероятность диагноза (значение обговаривалось с врачами)
     */
    private val minimalProbability = 0.95

    override fun predict(bundle: Bundle, take: Int): PredictDiagnosisResponse {
        val questionnaireResponse = bundle.resources(ResourceType.QuestionnaireResponse)
                .find { it.questionnaire == "Questionnaire/Severity_criteria" }
                ?: throw Exception("Could not predict diagnosis: no severity criteria questionnaire response found ")
        val complaints = questionnaireResponse.item
                .find { it.linkId == "Complaints" }
                ?.answer?.mapNotNull { it.valueString }
                ?: throw Exception("Could not predict diagnosis: no complaints provided")

        // todo: debug byAlternativesOrCode
        val complaintCodes = conceptService.byAlternativeOrDisplay(ValueSetName.COMPLAINTS, complaints)
        if (complaintCodes.isEmpty()) throw Error("Complaint codes not found")

        val diagnosisCodesList = codeMapService.icdByAllComplaints(complaintCodes, take)
        val diagnosisCodes = diagnosisCodesList.map { PredictedDiagnosis(code = it, system = "ValueSet/${ValueSetName.ICD_10}", probability = 1.0) }
        if (diagnosisCodes.size < take) {
            val moreDiagnosisCodes = codeMapService.icdByAnyComplaints(complaintCodes, diagnosisCodesList, take - diagnosisCodesList.size)
                    .mapNotNull {
                        PredictedDiagnosis(
                                code = it!!.diagnosisCode,
                                system = "ValueSet/${ValueSetName.ICD_10}",
                                probability = it.complaintCodeCount.toDouble() / complaintCodes.size.toDouble()
                        )
                    }
            return PredictDiagnosisResponse((diagnosisCodes + moreDiagnosisCodes)
                    .filter { it.probability > minimalProbability })
        }
        return PredictDiagnosisResponse(diagnosisCodes)
    }

    /**
     * Сохранить данные для предположения диагноза
     *
     * Данные извлекаются по patientId из ресурсов следующих типов:
     * - Observation
     * - QuestionnaireResponse
     * - Patient
     * - DiagnosticReport - диагноз ответственного врача
     * Их можно найти по активному обращению пациента (ClinicalImpression),
     * содержащего ссылки на перечисленные ресурсы.
     *
     */
    override fun saveTrainingSample(diagnosticReport: DiagnosticReport): Long? {
        diagnosticReport.subject.id()?.let { patientId ->
            patientService.byId(patientId).let { patient ->
                clinicalImpressionService.active(patientId).let { clinicalImpression ->
                    val observations = clinicalImpression.supportingInfo
                            .filter { it.type == ResourceType.ResourceTypeId.Observation }
                            .map { resourceService.byId(ResourceType.Observation, it.id()) }

                    val questionnaireResponse = clinicalImpression.supportingInfo
                            .filter { it.type == ResourceType.ResourceTypeId.QuestionnaireResponse }
                            .map { resourceService.byId(ResourceType.QuestionnaireResponse, it.id()) }
                            .find { it.questionnaire == "Questionnaire/Severity_criteria" }

                    val complaints = questionnaireResponse?.item
                            ?.find { it.linkId == "Complaints" }
                            ?.answer?.mapNotNull { it.valueString }
                            ?: throw Exception("Could not predict diagnosis: no complaints provided")

                    // заключительный диагноз всегда один
                    val diagnosisCode = diagnosticReport.conclusionCode.first().code()
                    val complaintCodes = conceptService.byAlternativeOrDisplay(ValueSetName.COMPLAINTS, complaints)
                    val codeMap = codeMapService.icdToComplaints(diagnosisCode)?.let { sourceCodes ->
                        val resultComplaints = mutableListOf<CodeMapTargetCode>()
                        complaintCodes.forEach { code ->
                            if (!sourceCodes.contains(code)) {
                                resultComplaints.add(CodeMapTargetCode(code))
                            }
                        }
                        // обновить code map с новыми жалобами
                        codeMapService.codeMap(ValueSetName.ICD_10, ValueSetName.COMPLAINTS, diagnosisCode).let {
                            resourceService.update(ResourceType.CodeMap, it.id) {
                                this.targetCode = targetCode.union(resultComplaints).toList()
                            }
                        }
                    }
                    if (codeMap == null) {
                        conceptService.byCode(ValueSetName.ICD_10, diagnosisCode).let {
                            resourceService.create(
                                CodeMap(
                                    id = "ICD-10_to_Complaints:$diagnosisCode",
                                    sourceUrl = "ValueSet/${ValueSetName.ICD_10.id}",
                                    targetUrl = "ValueSet/${ValueSetName.COMPLAINTS.id}",
                                    sourceCode = it.code,
                                    targetCode = complaintCodes.map { code -> CodeMapTargetCode(code) }
                                )
                            )
                        }
                    }
                    return toTrainingSample(observations, patient, diagnosticReport, questionnaireResponse)
                        .let { trainingSampleRepository.save(it) }.id
                }
            }
        }
        return null
    }

    /**
     * Преобразовать данные из ресурсов в [TrainingSample]
     *
     * На данный момент предположение диагноза будет происходить только
     * на основании списка жалоб [TrainingSample.complaints],
     * остальные данные хранятся для обучения модели в будущем
     */
    private fun toTrainingSample(
            observations: List<Observation>,
            patient: Patient,
            diagnosticReport: DiagnosticReport,
            questionnaireResponse: QuestionnaireResponse?
    ) = TrainingSample(
            systolicBP = observations.find { it.code.code() == ObservationType.BLOOD_PRESSURE_UPPER_LIMIT.id }?.valueInteger,
            diastolicBP = observations.find { it.code.code() == ObservationType.BLOOD_PRESSURE_LOWER_LIMIT.id }?.valueInteger,
            age = patient.age,
            gender = patient.gender.name,
            weight = observations.find { it.code.code() == ObservationType.WEIGHT.id }?.valueInteger,
            height = observations.find { it.code.code() == ObservationType.HEIGHT.id }?.valueInteger,
            pulseRate = observations.find { it.code.code() == ObservationType.PULSE_RATE.id }?.valueInteger,
            heartRate = observations.find { it.code.code() == ObservationType.HEART_RATE.id }?.valueInteger,
            breathingRate = observations.find { it.code.code() == ObservationType.BREATHING_RATE.id }?.valueInteger,
            bodyTemperature = observations.find { it.code.code() == ObservationType.BODY_TEMPERATURE.id }?.valueQuantity?.value,
            bloodOxygenSaturation = observations.find { it.code.code() == ObservationType.BLOOD_OXYGEN_SATURATION.id }?.valueInteger,
            consciousnessAssessment = questionnaireResponse?.item?.find { it.linkId == "Consciousness_assessment" }?.answer?.first()?.valueCoding?.code,
            upperRespiratoryAirway = questionnaireResponse?.item?.find { it.linkId == "Upper_respiratory_airway" }?.answer?.first()?.valueCoding?.code,
            painIntensity = observations.find { it.code.code() == ObservationType.PAIN_INTENSITY.id }?.valueInteger,
            patientCanStand = questionnaireResponse?.item?.find { it.linkId == "Patient_can_stand" }?.answer?.first()?.valueCoding?.code,
            complaints = questionnaireResponse?.item?.find { it.linkId == "Complaints" }?.answer?.mapNotNull { it.valueString }?.joinToString(", "),
            severity = questionnaireResponse?.item?.find { it.linkId == "Severity" }?.answer?.first()?.valueCoding?.code,
            diagnosis = diagnosticReport.conclusionCode.first().code()
    )
}
