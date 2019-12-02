package ru.viscur.dh.apps.rfidlocationdevice

import java.io.BufferedReader
import java.io.File
import java.util.regex.Pattern

/**
 * Вспомогательный класс для построчного чтения файла логов
 * Пустые строки и части строк следующие за символом '#' игнорируются
 */
class LogFileReader(val fileName: String) {

    var current: String? = null
    private val delimiter: Pattern = Pattern.compile("#")!!
    private val reader: BufferedReader = File(fileName).bufferedReader()

    init {
        next()
    }

    fun next() {
        current = readNext()
    }

    private fun readNext(): String? {
        while (true) {
            val line = reader.readLine() ?: return null
            val cleaned = line.split(delimiter, 2)[0]
            if (cleaned.isNotBlank()) return line
        }
    }
}
