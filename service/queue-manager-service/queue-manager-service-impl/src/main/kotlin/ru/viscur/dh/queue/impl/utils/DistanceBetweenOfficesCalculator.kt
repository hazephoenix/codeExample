package ru.viscur.dh.queue.impl.utils

import ru.viscur.dh.datastorage.api.util.*

/**
 * Created at 05.11.2019 17:26 by SherbakovaMA
 *
 * Интсрумент для определения расстояния между кабинетами
 */
class DistanceBetweenOfficesCalculator {
    companion object {
        private val distances = listOf(
                DistanceBetweenOffices(listOf(RECEPTION), listOf(OFFICE_101, OFFICE_104), 0.1),
                DistanceBetweenOffices(listOf(RECEPTION), listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), 0.2),
                DistanceBetweenOffices(listOf(RECEPTION), listOf(OFFICE_151, OFFICE_149), 0.4),
                DistanceBetweenOffices(listOf(RECEPTION), listOf(OFFICE_116, OFFICE_117), 0.5),
                DistanceBetweenOffices(listOf(RECEPTION), listOf(OFFICE_202), 0.7),
                DistanceBetweenOffices(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), 0.3),
                DistanceBetweenOffices(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_151, OFFICE_149), 0.5),
                DistanceBetweenOffices(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_116, OFFICE_117), 0.6),
                DistanceBetweenOffices(listOf(OFFICE_101, OFFICE_104), listOf(OFFICE_202), 0.8),
                DistanceBetweenOffices(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_151, OFFICE_149), 0.1),
                DistanceBetweenOffices(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_116, OFFICE_117), 0.5),
                DistanceBetweenOffices(listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130), listOf(OFFICE_202), 0.7),
                DistanceBetweenOffices(listOf(OFFICE_151, OFFICE_149), listOf(OFFICE_116, OFFICE_117), 0.6),
                DistanceBetweenOffices(listOf(OFFICE_151, OFFICE_149), listOf(OFFICE_202), 0.8),
                DistanceBetweenOffices(listOf(OFFICE_116, OFFICE_117), listOf(OFFICE_202), 0.9)
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
        return distances.find { from in it.from && to in it.to || from in it.to && to in it.from }?.value ?: 0.0
    }

    class DistanceBetweenOffices(val from: List<String>, val to: List<String>, val value: Double)
}
