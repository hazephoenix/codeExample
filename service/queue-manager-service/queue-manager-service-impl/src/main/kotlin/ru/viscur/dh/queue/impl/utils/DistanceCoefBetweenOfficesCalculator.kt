package ru.viscur.dh.queue.impl.utils

import ru.viscur.dh.datastorage.api.util.*

/**
 * Created at 05.11.2019 17:26 by SherbakovaMA
 *
 * Интсрумент для определения коэффициента дальности между кабинетами
 */
class DistanceCoefBetweenOfficesCalculator {
    companion object {


        val GROUP_1 = listOf(
                RECEPTION,
                RED_ZONE,
                YELLOW_ZONE_SECTION_1,
                YELLOW_ZONE_SECTION_2,
                YELLOW_ZONE_SECTION_3,
                YELLOW_ZONE_SECTION_4,
                YELLOW_ZONE_SECTION_5,
                YELLOW_ZONE_SECTION_6,
                GREEN_ZONE
        )
        val GROUP_2 = listOf(OFFICE_101, OFFICE_104)
        val GROUP_3 = listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130)
        val GROUP_4 = listOf(OFFICE_150, OFFICE_151, OFFICE_149)
        val GROUP_5 = listOf(OFFICE_116, OFFICE_117)
        val GROUP_6 = listOf(OFFICE_202)
        val GROUP_7 = listOf(OFFICE_120, OFFICE_120)
        private val coefs = listOf(
                DistanceCoef(GROUP_1, GROUP_2, 0.1),
                DistanceCoef(GROUP_1, GROUP_3, 0.2),
                DistanceCoef(GROUP_1, GROUP_4, 0.4),
                DistanceCoef(GROUP_1, GROUP_5, 0.5),
                DistanceCoef(GROUP_1, GROUP_6, 0.7),
                DistanceCoef(GROUP_1, GROUP_7, 0.8),
                DistanceCoef(GROUP_2, GROUP_3, 0.3),
                DistanceCoef(GROUP_2, GROUP_4, 0.5),
                DistanceCoef(GROUP_2, GROUP_5, 0.6),
                DistanceCoef(GROUP_2, GROUP_6, 0.8),
                DistanceCoef(GROUP_2, GROUP_7, 0.9),
                DistanceCoef(GROUP_3, GROUP_4, 0.1),
                DistanceCoef(GROUP_3, GROUP_5, 0.5),
                DistanceCoef(GROUP_3, GROUP_6, 0.7),
                DistanceCoef(GROUP_3, GROUP_7, 0.6),
                DistanceCoef(GROUP_4, GROUP_5, 0.6),
                DistanceCoef(GROUP_4, GROUP_6, 0.8),
                DistanceCoef(GROUP_4, GROUP_7, 0.4),
                DistanceCoef(GROUP_5, GROUP_6, 0.9),
                DistanceCoef(GROUP_5, GROUP_7, 1.0),
                DistanceCoef(GROUP_6, GROUP_7, 1.1)
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
