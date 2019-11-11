package ru.viscur.dh.queue.impl.utils

import ru.viscur.dh.datastorage.api.util.*

/**
 * Created at 05.11.2019 17:26 by SherbakovaMA
 *
 * Интсрумент для определения коэффициента дальности между кабинетами
 */
class DistanceCoefBetweenOfficesCalculator {
    companion object {
        private val coefs = listOf(
                DistanceCoef(listOf(RECEPTION), listOf(OFFICE_101, OFFICE_104), 0.1),
                DistanceCoef(listOf(RECEPTION), listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), 0.2),
                DistanceCoef(listOf(RECEPTION), listOf(OFFICE_150, OFFICE_151, OFFICE_149), 0.4),
                DistanceCoef(listOf(RECEPTION), listOf(OFFICE_116, OFFICE_117), 0.5),
                DistanceCoef(listOf(RECEPTION), listOf(OFFICE_202), 0.7),
                DistanceCoef(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), 0.3),
                DistanceCoef(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_150, OFFICE_151, OFFICE_149), 0.5),
                DistanceCoef(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_116, OFFICE_117), 0.6),
                DistanceCoef(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_202), 0.8),
                DistanceCoef(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_150, OFFICE_151, OFFICE_149), 0.1),
                DistanceCoef(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_116, OFFICE_117), 0.5),
                DistanceCoef(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_202), 0.7),
                DistanceCoef(listOf(OFFICE_150, OFFICE_151, OFFICE_149), listOf(OFFICE_116, OFFICE_117), 0.6),
                DistanceCoef(listOf(OFFICE_150, OFFICE_151, OFFICE_149), listOf(OFFICE_202), 0.8),
                DistanceCoef(listOf(OFFICE_116, OFFICE_117), listOf(OFFICE_202), 0.9)
        )
    }

    /**
     * Определить расстояние между кабинетами
     * Чем больше получено значение тем дальше кабинеты
     * Порядок передачи параметров в from-to или to-from не важен
     * @param from id первого кабинета
     * @param to id второго кабинета
     */
    fun calculate(from: String, to: String): Double {
        if (from == to) return 0.0
        return coefs.find { from in it.from && to in it.to || from in it.to && to in it.from }?.value ?: 0.0
    }

    /**
     * Описание коэффициента дальности между кабинетами
     * @param from откуда - id кабинета
     * @param to куда
     * @param value значение коэффициента
     */
    private class DistanceCoef(val from: List<String>, val to: List<String>, val value: Double)
}
