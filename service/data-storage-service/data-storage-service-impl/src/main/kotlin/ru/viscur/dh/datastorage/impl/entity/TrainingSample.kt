package ru.viscur.dh.datastorage.impl.entity

import javax.persistence.*

/**
 * Обучающая выборка для подсказки предварительного диагноза
 *
 * @param systolicBP Систолическое артериальное давлени
 * @param diastolicBP Диастолическое артериальное давление
 * @param age Возраст
 * @param gender Пол
 * @param weight Вес
 * @param height Рост
 * @param pulseRate Частота пульса
 * @param heartRate Частота сердечных сокращений
 * @param breathingRate Частота дыхания
 * @param upperRespiratoryAirway Верхние дыхательные пути
 * @param consciousnessAssessment Оценка уровня сознания
 * @param bloodOxygenSaturation Уровень оксигенации крови
 * @param bodyTemperature Температура
 * @param painIntensity Интенсивность боли
 * @param patientCanStand Пациент может стоять
 * @param complaints Жалобы пациента
 * @param severity Степень тяжести пациента
 * @param diagnosis Код диагноза МКБ-10 - обязательное поле
 */
@Entity
@Table(name = "training_sample")
@SequenceGenerator(name = "pk_seq", sequenceName = "pk_seq", allocationSize = 1)
data class TrainingSample(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq")
        var id: Long = 0L,
        @Column(name = "systolic_bp")
        var systolicBP: Int? = null,
        @Column(name = "diastolic_bp")
        var diastolicBP: Int? = null,
        @Column
        var age: Int? = null,
        @Column
        var gender: String? = null,
        @Column
        var weight: Int? = null,
        @Column
        var height: Int? = null,
        @Column(name = "pulse_rate")
        var pulseRate: Int? = null,
        @Column(name = "heart_rate")
        var heartRate: Int? = null,
        @Column(name = "breathing_rate")
        var breathingRate: Int? = null,
        @Column(name = "upper_respiratory_airway")
        var upperRespiratoryAirway: String? = null,
        @Column(name = "consciousness_assessment")
        var consciousnessAssessment: String? = null,
        @Column(name = "blood_oxygen_saturation")
        var bloodOxygenSaturation: Int? = null,
        @Column(name = "body_temperature")
        var bodyTemperature: Double? = null,
        @Column(name = "pain_intensity")
        var painIntensity: Int? = null,
        @Column(name = "patient_can_stand")
        var patientCanStand: String? = null,
        @Column
        var complaints: String? = null,
        @Column
        var severity: String? = null,
        @Column
        var diagnosis: String
)