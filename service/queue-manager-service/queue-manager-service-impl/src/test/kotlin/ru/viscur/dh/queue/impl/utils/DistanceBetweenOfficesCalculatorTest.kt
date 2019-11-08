package ru.viscur.dh.queue.impl.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.viscur.dh.datastorage.api.util.OFFICE_101
import ru.viscur.dh.datastorage.api.util.OFFICE_104
import ru.viscur.dh.datastorage.api.util.OFFICE_149
import ru.viscur.dh.datastorage.api.util.RECEPTION

/**
 * Created at 06.11.2019 8:48 by SherbakovaMA
 *
 * Тест для [DistanceBetweenOfficesCalculator]
 */
@RunWith(Parameterized::class)
class DistanceBetweenOfficesCalculatorTest(val case: TestCase) {

    data class TestCase(
            val desc: String,
            val from: String,
            val to: String,
            val exp: Double
    )

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun testCases() = listOf(
                TestCase(desc = "Простое определение", from = OFFICE_101, to = OFFICE_149, exp = 0.5),
                TestCase(desc = "Обратный путь", from = OFFICE_149, to = OFFICE_101, exp = 0.5),
                TestCase(desc = "Источник и назначение из одной группы", from = OFFICE_101, to = OFFICE_104, exp = 0.0),
                TestCase(desc = "Источник и назначение совпадают", from = OFFICE_101, to = OFFICE_101, exp = 0.0),
                TestCase(desc = "Источник из фиктивного id", from = RECEPTION, to = OFFICE_101, exp = 0.1)
        )
    }

    @Test
    fun test() {
        assertEquals(case.exp, DistanceBetweenOfficesCalculator().calculate(case.from, case.to),
                "${case.desc}. wrong calculating of distance between ${case.from} and ${case.to}")
    }
}