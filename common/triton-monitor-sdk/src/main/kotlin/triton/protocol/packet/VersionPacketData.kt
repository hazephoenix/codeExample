package triton.protocol.packet

import triton.protocol.enums.ReturnCode

class VersionPacketData(
        /**
         * Код возврата
         */
        override val returnCode: ReturnCode,
        /**
         * Модель монитора
         */
        val model: UShort,

        /**
         * Версия ПО
         */
        val softwareVersion: Version
) : ReturnCodePacket() {

    /**
     * Версия
     */
    class Version(
            /**
             * Мажорная версия
             */
            val major: UShort,

            /**
             * Минорная версия
             */
            val minor: UShort
    )

}