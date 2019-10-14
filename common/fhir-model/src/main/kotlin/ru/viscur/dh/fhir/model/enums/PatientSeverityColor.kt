package ru.viscur.dh.fhir.model.enums

import ru.viscur.dh.fhir.model.enums.PatientSeverityColor.*

/**
 * Цвет степени тяжести пациента
 *
 * @property RED Красный - тяжелая степень тяжести, реанимационный
 * @property YELLOW Желтый - средней степени тяжести
 * @property GREEN Зеленый - удовлетворительное состояние
 */
enum class PatientSeverityColor(val translation: String) {
    RED("Красный"),
    YELLOW("Желтый"),
    GREEN("Зеленый")
}