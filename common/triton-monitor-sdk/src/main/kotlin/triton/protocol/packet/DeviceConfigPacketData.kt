package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class DeviceConfigPacketData(
        val returnCode: ReturnCode,
        val spO2: LimitSet,
        val pr: LimitSet,
        val ndsys: LimitSet,
        val nsys: LimitSet,
        val rsp: LimitSet,
        val etCO2: LimitSet,
        val fiCO2: LimitSet,
        val temperature1: LimitSet,
        val temperature2: LimitSet,
        val timeOfPressureMeasuring: UByte,
        val timeToPressureMeasuring: UByte,
        val soundSet: SoundSet
) {

    class LimitSet(
            /**
             * Верхний порог
             */
            var up: UShort,

            /**
             * Нижний порог
             */
            var down: UShort,

            /**
             *  Тревога включена
             */
            var alarm: Boolean
    )

    class SoundSet(
            /**
             * Уровень громкости тревоги в %(0-10-20-..100)
             */
            var alarmVolume: UByte,

            /**
             * Уровень громкости сообщений в %(0-10-20-..100)
             */
            var messageVolume: UByte,

            /**
             * Уровень громкости сигнала в %(0-10-20-..100)
             */
            var beepVolume: UByte
    )
}