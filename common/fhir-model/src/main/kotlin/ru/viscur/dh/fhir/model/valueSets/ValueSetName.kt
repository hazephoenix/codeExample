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
     * Оценка уровня сознания
     */
    CONSCIOUSNESS_ASSESSMENT("Consciousness_assessment"),
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
    SEVERITY("Severity"),
    /**
     * Жалобы пациента
     */
    COMPLAINTS("Complaints"),
    /**
     * Типы мест
     */
    LOCATION_TYPE("Location_types"),
    /**
     * Коды настроек системы
     */
    CONFIG_CODES("Config_codes")
}