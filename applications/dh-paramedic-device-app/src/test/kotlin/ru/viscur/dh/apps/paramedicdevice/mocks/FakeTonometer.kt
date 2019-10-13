package ru.viscur.dh.apps.paramedicdevice.mocks

import ru.viscur.dh.apps.paramedicdevice.dto.*
import java.lang.Thread.sleep
import java.util.*

/**
 * Created at 30.09.2019 15:52 by TimochkinEA
 */
class FakeTonometer(
        override val delayToResult: Long = 20000L
) : FakeMedDevice {
    override fun take(): Observation {
        sleep(delayToResult)
        return Observation(
                identifier = Identifier(UUID.randomUUID().toString()),
                component = listOf(
                        Component(
                                code = listOf(
                                        Code(
                                                coding = listOf(
                                                        Coding(
                                                                system = "http://loinc.org",
                                                                code = "8480-6",
                                                                display = "Систолическое давление"
                                                        )
                                                )
                                        )
                                ),
                                valueQuantity = ValueQuantity(
                                        value = 107,
                                        unit = "мм рт. ст"
                                )
                        ),
                        Component(
                                code = listOf(
                                        Code(
                                                coding = listOf(
                                                        Coding(
                                                                system = "http://loinc.org",
                                                                code = "8462-4",
                                                                display = "Диастолическое давление"
                                                        )
                                                )
                                        )
                                ),
                                valueQuantity = ValueQuantity(
                                        value = 60,
                                        unit = "мм рт. ст"
                                )
                        )
                )
        )
    }
}
