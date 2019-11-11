package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.enums.Severity
import java.util.*

/**
 * Created at 06.11.2019 12:04 by SherbakovaMA
 *
 * Сервис для оценки предположительной продолжительности проведения услуги
 * Оперирует историей [ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory]
 * и дефолтными значениями [ru.viscur.dh.datastorage.impl.entity.ObservationDefaultDuration]
 */
interface ObservationDurationEstimationService {

    fun deleteAllHistory()

    fun saveToHistory(patientId: String, code: String, diagnosis: String, severity: Severity, start: Date, end: Date)

    /**
     * Определение среднего значения для заданных параметров по истории
     * Сначала поиск по полному соответсвию
     * если не находит, то по соответствию код+диагноз
     * если не находит, то по соответствию код+степень тяжести
     * если не находит, то по соответствию код
     * если не находит, то возвращает null
     */
    fun avgByHistory(code: String, diagnosis: String, severity: Severity): Int?

    /**
     * Оценка продолжительности
     * Ищет среднее по истории через [avgByHistory]
     * Если нет никакого совпадения, то оперирует дефолтными значениями в [ru.viscur.dh.datastorage.impl.entity.ObservationDefaultDuration]
     * Дефолтные значения указаны не для всех, поэтому если и там не находит,
     * то дает фиксированное значение
     */
    fun estimate(code: String, diagnosis: String, severity: Severity): Int
}