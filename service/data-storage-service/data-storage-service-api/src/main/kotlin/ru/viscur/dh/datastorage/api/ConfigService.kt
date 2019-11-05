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
     */
    fun write(code: String, value: String?)
}