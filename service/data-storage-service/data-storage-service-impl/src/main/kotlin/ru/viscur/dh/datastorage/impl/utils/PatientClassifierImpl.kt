package ru.viscur.dh.datastorage.impl.utils

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.valueSets.*

@Component
class PatientClassifierImpl(
        private val diagnosisPredictor: DiagnosisPredictor
) : PatientClassifier {

    override fun classify(bundle: Bundle, takeSyndromes: Int): SeverityResponse {
        val colors = bundle.entry.mapNotNull {
            when(it.resource) {
                is Observation -> getColorByValue(it.resource as Observation)
                is QuestionnaireResponse -> checkQuestionnaireResponse(it.resource as QuestionnaireResponse)
                else -> null
            }
        }

        getResultSeverity(colors)?.let {
            return SeverityResponse(
                    severity = Concept(
                            code = it.severity.toString(),
                            system = "ValueSet/${ValueSetName.SEVERITY.id}",
                            display = it.severity.display
                    ),
                    mainSyndrome = diagnosisPredictor.predict(bundle, takeSyndromes),
                    severityReason = it.reason
            )
        } ?: throw Error("Не удалось определить степень тяжести пациента")
    }

    /**
     * Определить степень тяжести по ответам в опроснике фельдшера
     *
     * @param response Опросник [QuestionnaireResponse]
     * @return [PatientSeverity] Степень тяжести пациента
     */
    private fun checkQuestionnaireResponse(response: QuestionnaireResponse): PatientSeverity? {
        val colors = response.item.mapNotNull { item ->
            responseToColor
                    .find { item.answer.first().valueCoding?.code == it.code && it.linkId == item.linkId }
                    ?.let {
                        PatientSeverity(it.severity, it.reason)
                    }
        }
        return getResultSeverity(colors)
    }

    /**
     * Определить степень тяжести пациента по значению измерения
     *
     * @param observation [Observation] Измерение
     * @return [PatientSeverity] Степень тяжести пациента
     */
    private fun getColorByValue(observation: Observation): PatientSeverity? {
        return responseToColor
                .filter {
                    it.linkId == observation.code.coding.first().code
                }.find { answer ->
                    val value = when {
                        observation.valueQuantity != null -> observation.valueQuantity?.value
                        observation.valueInteger != null -> observation.valueInteger?.toDouble()
                        else -> null
                    }
                    value?.let {
                        (answer.rangeFrom != null && answer.rangeTo != null &&
                                (answer.rangeFrom as Double..answer.rangeTo as Double).contains(value)) ||
                            (answer.rangeFrom != null && answer.rangeTo === null && value < answer.rangeFrom as Double) ||
                            (answer.rangeTo != null && answer.rangeFrom === null && value > answer.rangeTo as Double)
                    } ?: false
                }?.let {
                    PatientSeverity(it.severity, it.reason)
                }
    }

    /**
     * Определить результирующую степень тяжести пациента.
     *
     * В случае, если в наборе хотя бы один из критериев указывает
     * на высокую степерь тяжести - цвет красный. Если красных критериев нет,
     * но хотя бы один желтый - то цвет желтый.
     *
     * @param values Набор полученных значений степеней тяжести
     * @return [PatientSeverity] Степень тяжести пациента
     */
    private fun getResultSeverity(values: List<PatientSeverity>): PatientSeverity? {
        return when {
            values.any { it.severity == Severity.RED } -> getSeverity(Severity.RED, values)
            values.any { it.severity == Severity.YELLOW }  -> getSeverity(Severity.YELLOW, values)
            values.any { it.severity == Severity.GREEN } -> getSeverity(Severity.GREEN, values)
            else -> null
        }
    }

    /**
     * Вернуть степень тяжести пациента с указанием причины -
     * объединить причины по разным признакам в единую строку
     */
    private fun getSeverity(color: Severity, severityValues: List<PatientSeverity>): PatientSeverity {
        val reason = severityValues.filter { it.severity == color }.let { filtered ->
            filtered.joinToString(separator = ", ") { it.reason }
        }
        return PatientSeverity(color, reason)
    }
}