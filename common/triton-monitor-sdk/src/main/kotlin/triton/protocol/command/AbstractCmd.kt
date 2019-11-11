package triton.protocol.command

import java.nio.ByteBuffer

abstract class AbstractCmd {

    abstract val header: String

    /**
     * Кодирует команду в формат, для отправки устройству по UDP
     */
    fun encode(): ByteArray {
        val data: MutableList<Byte> = mutableListOf()
        data.addAll(header.toByteArray(Charsets.US_ASCII))
        encode(data)
        return data.toByteArray()
    }

    protected open fun encode(buffer: MutableList<Byte>) {}


    protected fun MutableList<Byte>.addAll(byteArray: ByteArray) {
        addAll(byteArray.toTypedArray())
    }
}