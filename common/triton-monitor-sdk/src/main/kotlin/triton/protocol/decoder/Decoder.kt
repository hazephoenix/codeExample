package triton.protocol.decoder

import java.net.DatagramPacket

interface Decoder<T> {
    fun isAcceptable(packet: DatagramPacket): Boolean
    fun decode(packet: DatagramPacket): T
}


fun DatagramPacket.hasSizeAndHeader(expectedSize: Int, expectedHeader: String): Boolean {
    if (this.length != expectedSize) {
        return false
    }
    val expectedHeaderBytes = expectedHeader.toByteArray(Charsets.US_ASCII)
    return expectedHeaderBytes
            .contentEquals(
                    this.data.copyOfRange(0, expectedHeaderBytes.size)
            )
}


fun readDoubleWordAsLong(data: ByteArray, fromIndex: Int): Long {
    var res: Long = 0
    for (i: Int in 0..3) {
        val b: Long = data[fromIndex + i].toLong()
        res = res or (b shl (i * 8))
    }
    return res
}

fun readWord(data: ByteArray, fromIndex: Int): UShort {
    // Конвертирование сначала в UByte а потом в UShort принципиально!!!
    // (связано с необходимостью сохранить бинарное представление байта.
    // В случае, если конвертировать Byte напрямую в UShort, то бит знака переедет в старшие разряды!)
    // Кто будет править, внимательно читайте доку к методам приведения типов
    val lb = data[fromIndex].toUByte().toUShort()
    val hb = (data[fromIndex + 1].toUByte().toUInt() shl 8).toUShort()
    return hb or lb


}

fun readByte(data: ByteArray, fromIndex: Int): UByte {
    return data[fromIndex].toUByte()
}

fun readDoubleWordAsInt(data: ByteArray, fromIndex: Int): Int {
    return (readDoubleWordAsLong(data, fromIndex) and 0x00000000ffffffff).toInt()
}

fun readWordAsInt(data: ByteArray, fromIndex: Int): Int {
    var res: Int = 0
    for (i: Int in 0..1) {
        val b: Int = data[fromIndex + i].toInt()
        res = res or (b shl (i * 8))
    }
    return res
}

fun readWordAsShort(data: ByteArray, fromIndex: Int): Short {
    return (readWordAsInt(data, fromIndex) and 0x0000ffff).toShort()
}

fun readByteAsShort(data: ByteArray, fromIndex: Int): Short {
    return data[fromIndex].toShort()
}

fun readDoubleWordAsLong(data: ByteArray, context: DecodeContext): Long =
        readDoubleWordAsLong(data, context.move(4))

fun readDoubleWordAsInt(data: ByteArray, context: DecodeContext): Int =
        readDoubleWordAsInt(data, context.move(4))

fun readWordAsInt(data: ByteArray, context: DecodeContext): Int =
        readWordAsInt(data, context.move(2))

fun readWordAsShort(data: ByteArray, context: DecodeContext): Short =
        readWordAsShort(data, context.move(2))

fun readWord(data: ByteArray, context: DecodeContext): UShort =
        readWord(data, context.move(2))

fun readByteAsShort(data: ByteArray, context: DecodeContext): Short =
        readByteAsShort(data, context.move(1))

fun readByte(data: ByteArray, context: DecodeContext): UByte =
        readByte(data, context.move(1))

fun readDoubleWord(data: ByteArray, context: DecodeContext): UInt {
    return readDoubleWordAsInt(data, context)
            .toUInt()
}

fun readWordArray(data: ByteArray, wordsToRead: Int, context: DecodeContext): UShortArray {
    val array = UShortArray(wordsToRead)
    for (i in 0 until wordsToRead) {
        array[i] = readWord(data, context)
    }
    return array
}

fun readByteArray(data: ByteArray, bytesToRead: Int, context: DecodeContext): UByteArray {
    val array = UByteArray(bytesToRead)
    for (i in 0 until bytesToRead) {
        array[i] = readByte(data, context)
    }
    return array
}

fun readShortArray(data: ByteArray, shortsToRead: Int, context: DecodeContext): ShortArray {
    val array = ShortArray(shortsToRead)
    for (i in 0 until shortsToRead) {
        array[i] = readWordAsShort(data, context)
    }
    return array
}


class DecodeContext(
        var position: Int
) {
    fun move(bytes: Int): Int {
        val prev = position
        position += bytes
        return prev
    }
}