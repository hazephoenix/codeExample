package ru.viscur.dh.apps.paramedicdevice.utils

/**
 * Transmission control ASCII-characters
 */
const val NUL = 0x00.toByte()
const val SOH = 0x01.toByte()
const val STX = 0x02.toByte() // Start of Text byte
const val ETX = 0x03.toByte() // End of Text byte
const val EOT = 0x04.toByte()
const val ENQ = 0x05.toByte()
const val ACK = 0x06.toByte() // Acknowledgment
const val LF = 0x0A.toByte()
const val CR = 0x0D.toByte()
const val NAK = 0x15.toByte() // Negative acknowledgment
const val SYN = 0x16.toByte()
const val RS = 0x1E.toByte()
const val addressByte = 0x30.toByte()

const val BCCStart = (0x01 xor 0x30 xor 0x30 xor 0x02 xor 0x53 xor 0x54 xor 0x03).toByte()
const val BCCStop = (0x01 xor 0x30 xor 0x30 xor 0x02 xor 0x53 xor 0x50 xor 0x03).toByte()
/**
 * Начать измерение
 */
val startMeasuringCmd = byteArrayOf(SYN, SYN, SOH, addressByte, addressByte, STX, 0x53, 0x54, ETX, BCCStart)
val stopMeasuringCmd = byteArrayOf(SYN, SYN, SOH, addressByte, addressByte, STX, 0x53, 0x50, ETX, BCCStop)
