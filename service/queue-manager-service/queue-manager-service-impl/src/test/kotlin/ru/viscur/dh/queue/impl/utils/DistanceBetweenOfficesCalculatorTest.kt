package ru.viscur.dh.queue.impl.utils

import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import ru.viscur.dh.queue.impl.OFFICE101
import ru.viscur.dh.queue.impl.OFFICE104
import ru.viscur.dh.queue.impl.OFFICE149
import ru.viscur.dh.queue.impl.RECEPTION

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
                TestCase(desc = "Простое определение", from = OFFICE101, to = OFFICE149, exp = 0.5),
                TestCase(desc = "Обратный путь", from = OFFICE149, to = OFFICE101, exp = 0.5),
                TestCase(desc = "Источник и назначение из одной группы", from = OFFICE101, to = OFFICE104, exp = 0.0),
                TestCase(desc = "Источник и назначение совпадают", from = OFFICE101, to = OFFICE101, exp = 0.0),
                TestCase(desc = "Источник из фиктивного id", from = RECEPTION, to = OFFICE101, exp = 0.1)
        )
    }

    @Test
    fun test() {
        assertEquals(case.exp, DistanceBetweenOfficesCalculator().calculate(case.from, case.to),
                "${case.desc}. wrong calculating of distance between ${case.from} and ${case.to}")
    }
}