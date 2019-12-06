package triton.protocol.packet

class WavePacketData1(
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
         * Волна RSPC (дыхание по сапнографу) 32-байта
         */
        val rspCWave: UByteArray

)