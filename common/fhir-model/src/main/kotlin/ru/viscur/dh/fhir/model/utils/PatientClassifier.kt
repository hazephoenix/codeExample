package ru.viscur.dh.fhir.model.utils

/*import org.springframework.http.*
import org.springframework.web.client.**/
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Классификатор, определяющий степень тяжести пациента [PatientSeverityColor]
 *
 * TODO навверно не в моделе он должен быть?
 */
class PatientClassifier {
    companion object {
        val mainSyndromePredictor = MainSyndromePredictor()
    }
    /**
     * Определить степень тяжести пациента
     *
     * @param bundle Контейнер с входными измерениями
     * @return [PatientSeverityColor] Степень тяжести пациента
     */
    fun classify(bundle: Bundle): SeverityResponse {
        TODO("Модель не должа зависить от HTTP, переделать")
        /*val colors = mutableListOf<PatientSeverity>()
        for (entry in bundle.entry) {
            val color = when(entry.resource) {
                is Observation -> getColorByValue(entry.resource)
                is QuestionnaireResponse -> checkQuestionnaireResponse(entry.resource)
                else -> null
            }
            color?.let { colors.add(color) }
        }
        getResultSeverity(colors)?.let {
            return SeverityResponse(
                    severity = Concept(
                            code = it.color.toString(),
                            system = "ValueSet/${ValueSetName.SEVERITY.id}",
                            display = it.color.translation
                    ),
                    mainSyndrome = mainSyndromePredictor.predict(),
                    severityReason = it.reason
            )
        } ?: throw HttpClientErrorException(
                HttpStatus.BAD_REQUEST,
                "Не удалось определить степень тяжести пациента"
        )*/
    }

    /**
     * Определить степень тяжести по ответам в опроснике фельдшера
     *
     * @param response Опросник [QuestionnaireResponse]
     * @return [PatientSeverity] Степень тяжести пациента
     */
    private fun checkQuestionnaireResponse(response: QuestionnaireResponse): PatientSeverity? {
        val colors = mutableListOf<PatientSeverity>()
        response.item?.forEach { item ->
            val code = item.answer[0].valueCoding?.code
            val color = when(item.linkId) {
                ValueSetName.UPPER_RESPIRATORY_AIRWAY.id -> {
                    val reason = "дыхательные пути"
                    when(code) {
                        "Airways_not_passable_(asphyxia)_or_not_breathing" -> PatientSeverity(PatientSeverityColor.RED, reason)
                        "Airways_passable" -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
                ValueSetName.CONSCIOUSNESS_ASSESSMENT.id -> {
                    val reason = "сознание"
                    when(code) {
                        "Coma,_ongoing_generalized_cramps" -> PatientSeverity(PatientSeverityColor.RED, reason)
                        "Stun" -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        "Clear_mind" -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
                ValueSetName.PAIN_INTENSITY_ASSESSMENT.id -> {
                    val reason = "уровень боли"
                    when(code) {
                        "Not_count" -> PatientSeverity(PatientSeverityColor.RED, reason)
                        "From_4_to_10" -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        "From_0_to_3" -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
                ValueSetName.PATIENT_CAN_STAND.id -> {
                    val reason = "опорная функция"
                    when(code) {
                        "Not_count" -> PatientSeverity(PatientSeverityColor.RED, reason)
                        "Cant_stand" -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        "Can_stand" -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
                else -> null
            }
            color?.let { colors.add(color) }
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
        return when(observation.code) {
            observationCodes.getValue(ValueSetName.HEART_RATE_PER_MINUTE.id) -> {
                val reason = "частота сердечных сокращений"
                observation.valueInteger?.let { rate ->
                    when {
                        rate > 150 || rate < 40 -> PatientSeverity(PatientSeverityColor.RED, reason)
                        rate in 120..150 || rate in 40..50 -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        rate in 51..119 -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }

                }
            }
            observationCodes.getValue(ValueSetName.BODY_TEMPERATURE.id) -> {
                observation.valueQuantity?.let { quantity ->
                    val temp = quantity.value
                    val reason = "температура тела"
                    when {
                        temp > 41 || temp < 35 -> PatientSeverity(PatientSeverityColor.RED, reason)
                        temp in 38.5..41.0 -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        temp in 35.1..38.4 -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
            }
            observationCodes.getValue(ValueSetName.BLOOD_PRESSURE_UPPER_LIMIT.id) -> {
                val reason = "артериальное давление"
                observation.valueInteger?.let { pressure ->
                    when {
                        pressure < 90 -> PatientSeverity(PatientSeverityColor.RED, reason)
                        else -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                    }
                }
            }
            observationCodes.getValue(ValueSetName.BREATHING_RATE.id) -> {
                val reason = "частота дыхания"
                observation.valueInteger?.let { rate ->
                    when {
                        rate > 30 -> PatientSeverity(PatientSeverityColor.RED, reason)
                        rate in 25..30 -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        rate < 25 -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
            }
            observationCodes.getValue(ValueSetName.BLOOD_OXYGENATION_LEVEL.id) -> {
                val reason = "уровень оксенизации крови"
                observation.valueInteger?.let { level ->
                    when {
                        level < 90 -> PatientSeverity(PatientSeverityColor.RED, reason)
                        level in 90..95 -> PatientSeverity(PatientSeverityColor.YELLOW, reason)
                        level > 95 -> PatientSeverity(PatientSeverityColor.GREEN, reason)
                        else -> null
                    }
                }
            }
            else -> null
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
    private fun getResultSeverity(values: MutableList<PatientSeverity>): PatientSeverity? {
        return when {
            values.any { it.color == PatientSeverityColor.RED } -> getSeverity(PatientSeverityColor.RED, values)
            values.any { it.color == PatientSeverityColor.YELLOW }  -> getSeverity(PatientSeverityColor.YELLOW, values)
            values.any { it.color == PatientSeverityColor.GREEN } -> getSeverity(PatientSeverityColor.GREEN, values)
            else -> null
        }
    }

    /**
     * Вернуть степень тяжести пациента с указанием причины -
     * объединить причины по разным признакам в единую строку
     */
    private fun getSeverity(color: PatientSeverityColor, severityValues: MutableList<PatientSeverity>): PatientSeverity {
        val reason = severityValues.filter { it.color == color }.let { filtered ->
            filtered.joinToString(separator = ", ") { it.reason }
        }
        return PatientSeverity(color, reason)
    }
}
