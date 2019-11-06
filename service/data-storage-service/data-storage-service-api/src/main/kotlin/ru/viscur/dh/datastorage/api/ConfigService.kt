package ru.viscur.dh.datastorage.api

/**
 * Created at 05.11.2019 14:53 by SherbakovaMA
 *
 * Сервис для настроек системы
 */
interface ConfigService {

    /**
     * Чтение настройки
     */
    fun read(code: String): String?

    /**
     * Запись настройки
     * Если value = null, то происходит удаление настройки
     */
    fun write(code: String, value: String? = null)

    /**
     * Удаление
     */
    fun delete(code: String) = write(code)
}