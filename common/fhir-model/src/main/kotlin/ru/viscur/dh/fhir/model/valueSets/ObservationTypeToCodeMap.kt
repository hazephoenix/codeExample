package ru.viscur.dh.fhir.model.valueSets

import ru.viscur.dh.fhir.model.type.*

/**
 * Категория обследования - жизненно важные показатели
 */
const val VITAL_SIGNS = "vital-signs"

/**
 * Соответсвие типа и кода обследования
 */
val observationCodes = mapOf(
        /**
         * Артериальное давление верхняя граница
         */
        ValueSetName.BLOOD_PRESSURE_UPPER_LIMIT.id to CodeableConcept(ValueSetName.BLOOD_PRESSURE_UPPER_LIMIT.id, VITAL_SIGNS),
        /**
         * Частота сердечных сокращений
         */
        ValueSetName.HEART_RATE_PER_MINUTE.id to CodeableConcept(ValueSetName.HEART_RATE_PER_MINUTE.id, VITAL_SIGNS),
        /**
         * Уровень оксигенации крови
         */
        ValueSetName.BLOOD_OXYGENATION_LEVEL.id to CodeableConcept(ValueSetName.BLOOD_OXYGENATION_LEVEL.id, VITAL_SIGNS),
        /**
         * Частота дыхания
         */
        ValueSetName.BREATHING_RATE.id to CodeableConcept(ValueSetName.BREATHING_RATE.id, VITAL_SIGNS),
        /**
         * Температура тела
         */
        ValueSetName.BODY_TEMPERATURE.id to CodeableConcept(ValueSetName.BODY_TEMPERATURE.id, VITAL_SIGNS)
)