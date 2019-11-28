package triton.protocol.packet

class WavePacketData3(
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
        val co2Wave: UByteArray,

        /**
         * Волна ECG2 (ЭКГ) 128-байт
         */
        val ecg2Wave: UByteArray,

        /**
         * Волна ECG3 (ЭКГ) 128-байт
         */
        val ecg3Wave: UByteArray
)