package ru.viscur.dh.fhir.model.valueSets

/**
 * Названия типов обследований в [ValueSetName.OBSERVATION_TYPES] (ValueSet/Observation_types)
 */
enum class ObservationType(val id: String) {
    /**
     * Частота сердечных сокращений
     */
    HEART_RATE("Heart_rate"),
    /**
     * Частота пульса
     */
    PULSE_RATE("Pulse_rate"),
    /**
     * Частота дыхания
     */
    BREATHING_RATE("Breathing_rate"),
    /**
     * Уровень оксигенации крови
     */
    BLOOD_OXYGEN_SATURATION("Blood_oxygen_saturation"),
    /**
     * Артериальное давление верхняя граница
     */
    BLOOD_PRESSURE_UPPER_LIMIT("Blood_pressure_upper_limit"),
    /**
     * Артериальное давление нижняя граница
     */
    BLOOD_PRESSURE_LOWER_LIMIT("Blood_pressure_lower_limit"),
    /**
     * Температура тела
     */
    BODY_TEMPERATURE("Body_temperature"),
    /**
     * Вес
     */
    WEIGHT("Weight"),
    /**
     * Рост
     */
    HEIGHT("Height")
}