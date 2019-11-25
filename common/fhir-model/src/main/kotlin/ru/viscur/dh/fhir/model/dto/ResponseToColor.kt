package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Объект для таблицы соответствия степени тяжести пациента
 * и ответов опросника фельдшера
 *
 * @param severity степень тяжести
 * @param linkId ссылка на вопрос в опроснике
 * @param code код ответа
 * @param reason причина
 * @param rangeFrom верхняя граница нормы для числовых значений
 * @param rangeTo нижняя граница нормы для числовых значений
 */
data class ResponseToColor(
        val severity: Severity,
        val linkId: String,
        val code: String? = null,
        val reason: String,
        val rangeFrom: Double? = null,
        val rangeTo: Double? = null
)

/**
 * Таблица значений [ResponseToColor]
 */
val responseToColor = listOf(
        ResponseToColor(
                Severity.RED,
                ValueSetName.UPPER_RESPIRATORY_AIRWAY.id,
                "Airways_not_passable_(asphyxia)_or_not_breathing",
                "дыхательные пути не проходимы"
        ),
        ResponseToColor(
                Severity.GREEN,
                ValueSetName.UPPER_RESPIRATORY_AIRWAY.id,
                "Airways_passable",
                "дыхательные пути проходимы"
        ),
        ResponseToColor(
                Severity.RED,
                ValueSetName.CONSCIOUSNESS_ASSESSMENT.id,
                "Coma,_ongoing_generalized_cramps",
                "кома, продолжающиеся генерализованные судороги"
        ),
        ResponseToColor(
                Severity.YELLOW,
                ValueSetName.CONSCIOUSNESS_ASSESSMENT.id,
                "Stun",
                "оглушение, сопор"
        ),
        ResponseToColor(
                Severity.GREEN,
                ValueSetName.CONSCIOUSNESS_ASSESSMENT.id,
                "Clear_mind",
                "ясное сознание"
        ),
        ResponseToColor(
                Severity.YELLOW,
                ValueSetName.PATIENT_CAN_STAND.id,
                "Cant_stand",
                "не может стоять"
        ),
        ResponseToColor(
                Severity.GREEN,
                ValueSetName.PATIENT_CAN_STAND.id,
                "Can_stand",
                "может стоять"
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.PAIN_INTENSITY.id,
                null,
                "уровень боли 0-3",
                0.0,
                3.0
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.PAIN_INTENSITY.id,
                null,
                "уровень боли 4-10",
                3.0,
                10.0
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.HEART_RATE.id,
                null,
                "частота сердечных сокращений критична",
                null,
                150.0
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.HEART_RATE.id,
                null,
                "частота сердечных сокращений критична",
                40.0,
                null
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.HEART_RATE.id,
                null,
                "частота сердечных сокращений повышена",
                120.0,
                150.0
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.HEART_RATE.id,
                null,
                "частота сердечных сокращений повышена",
                40.0,
                50.0
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.HEART_RATE.id,
                null,
                "частота сердечных сокращений в норме",
                51.0,
                119.0
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.BODY_TEMPERATURE.id,
                null,
                "высокая температура тела",
                null,
                41.0
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.BODY_TEMPERATURE.id,
                null,
                "низкая температура тела",
                35.1,
                null
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.BODY_TEMPERATURE.id,
                null,
                "температура тела повышена",
                38.5,
                41.0
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.BODY_TEMPERATURE.id,
                null,
                "температура тела приемлема",
                35.1,
                38.4
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.BLOOD_PRESSURE_UPPER_LIMIT.id,
                null,
                "пониженное давление",
                90.0,
                null
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.BLOOD_PRESSURE_UPPER_LIMIT.id,
                null,
                "давление в норме",
                null,
                90.0
        ),
        // пограничное значение
        ResponseToColor(
                Severity.GREEN,
                ObservationType.BLOOD_PRESSURE_UPPER_LIMIT.id,
                null,
                "давление в норме",
                90.0,
                90.0
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.BREATHING_RATE.id,
                null,
                "слишком частое дыхание",
                null,
                30.0
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.BREATHING_RATE.id,
                null,
                "учащенное дыхание",
                25.0,
                30.0
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.BREATHING_RATE.id,
                null,
                "частота дыхания в норме",
                25.0,
                null
        ),
        ResponseToColor(
                Severity.RED,
                ObservationType.BLOOD_OXYGEN_SATURATION.id,
                null,
                "уровень оксенизации крови низкий",
                90.0,
                null
        ),
        ResponseToColor(
                Severity.YELLOW,
                ObservationType.BLOOD_OXYGEN_SATURATION.id,
                null,
                "уровень оксенизации крови понижен",
                90.0,
                95.0
        ),
        ResponseToColor(
                Severity.GREEN,
                ObservationType.BLOOD_OXYGEN_SATURATION.id,
                null,
                "уровень оксенизации крови приемлемый",
                null,
                95.0
        )
)
