package ru.viscur.dh.fhir.model.enums

/**
 * Created at 15.10.2019 8:47 by SherbakovaMA
 *
 * Степень тяжести пациента
 * Коды из system = 'ValueSet/Severity'
 *
 * @property RED Красный - тяжелая степень тяжести, реанимационный
 * @property YELLOW Желтый - средней степени тяжести
 * @property GREEN Зеленый - удовлетворительное состояние
 */
enum class Severity(val translation: String) {
    RED("Красный"),
    YELLOW("Желтый"),
    GREEN("Зеленый")
}