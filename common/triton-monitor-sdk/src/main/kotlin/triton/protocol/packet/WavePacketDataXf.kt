package triton.protocol.packet

class WavePacketDataXf(
        /**
         * Порядковый номер пакета волны
         */
        val packetNumber: UShort,

        /**
         * Волна PPG (фотоплетизмограмма) 64-байта
         */
        val ppgWave: UByteArray,

        /**
         * Волна ECG (ЭКГ) 128-байт
         */
        val ecgWave: UByteArray,

        /**
         * Волна RSPECG (дыхание по ЭКГ) 32-байта
         */
        val rspEcgWave: UByteArray,

        /**
         * Волна CO2 32-байта
         */
        val co2SmWave: UByteArray,

        /**
         * Волна O2 64-байтa
         */
        val co2Wave: UShortArray,

        /**
         * Волна O2 64-байтa
         */
        val o2Wave: UShortArray,

        /**
         * Волна Flow 64-байтa
         */
        val flowWave: ShortArray,

        /**
         * Волна Pressure 64-байтa
         */
        val flowPressure: ShortArray
)