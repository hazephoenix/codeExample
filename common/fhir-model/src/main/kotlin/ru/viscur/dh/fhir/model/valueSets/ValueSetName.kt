package ru.viscur.dh.fhir.model.valueSets

/**
 * Created at 07.10.2019 14:18 by SherbakovaMA
 *
 * Перечисление справочников системы с id
 * этот же id в [ru.viscur.dh.fhir.model.entity.Concept.system] и в [ru.viscur.dh.fhir.model.type.Coding.system]
 * в формате "ValueSet/id"
 */
enum class ValueSetName(val id: String) {
    /**
     * МКБ-10
     */
    ICD_10("ICD-10"),
    /**
     * Типы идентификаторов [Identifier.type][ru.viscur.dh.fhir.model.type.Identifier.type]. Его значения в [IdentifierType]
     */
    IDENTIFIER_TYPES("Identifier_types"),
    /**
     * Типы согласий. Его коды исп-ся в [Consent.category][ru.viscur.dh.fhir.model.entity.Consent.category]
     */
    CONSENT_CATEGORIES("Consent_categories"),
    /**
     * Квалификации мед. работника. Его коды исп-ся в [PractitionerQualification.code][ru.viscur.dh.fhir.model.type.PractitionerQualification.code]
     */
    PRACTITIONER_QUALIFICATIONS("Practitioner_qualifications"),
    /**
     * Типы процедур/услуг. Его коды исп-ся в [.Observation.code][ru.viscur.dh.fhir.model.entity.Observation.code]
     */
    OBSERVATION_TYPES("Observation_types"),
    /**
     * Результат осмотра верхних дыхательных путей
     */
    UPPER_RESPIRATORY_AIRWAY("Upper_respiratory_airway"),
    /**
     * Частота дыхания
     */
    BREATHING_RATE("Breathing_rate"),
    /**
     * Уровень оксигенации крови
     */
    BLOOD_OXYGENATION_LEVEL("Blood_oxygenation_level"),
    /**
     * Частота сердечных сокращений в минуту
     */
    HEART_RATE_PER_MINUTE("Heart_rate_per_minute"),
    /**
     * Артериальное давление верхняя граница
     */
    BLOOD_PRESSURE_UPPER_LIMIT("Blood_pressure_upper_limit"),
    /**
     * Оценка уровня сознания
     */
    CONSCIOUSNESS_ASSESSMENT("Consciousness_assessment"),
    /**
     * Температура тела
     */
    BODY_TEMPERATURE("Body_temperature"),
    /**
     * Оценка интенсивности боли
     */
    PAIN_INTENSITY_ASSESSMENT("Pain_intensity_assessment"),
    /**
     * Пациент может стоять
     */
    PATIENT_CAN_STAND("Patient_can_stand"),
    /**
     * Результат сортировки (степень тяжести пациента)
     */
    SEVERITY("Severity")
}