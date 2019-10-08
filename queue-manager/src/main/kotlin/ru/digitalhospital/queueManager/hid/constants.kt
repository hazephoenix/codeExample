package ru.digitalhospital.queueManager.hid

/**
 * Включить модуль
 */
const val CMD_ON: Byte = 0x01

/**
 * Выключить модуль
 */
const val CMD_OFF: Byte = 0x00

/**
 * Запустить модуль
 */
const val CMD_START: Byte = 0x02

/**
 * Остановить модуль
 */
const val CMD_STOP: Byte = 0x03

/**
 * Получить информацию о модуле
 */
const val CMD_GET_INFO: Byte = 0x05

/**
 * ID отчета для команд
 */
const val COMMAND_REPORT_ID: Byte = 0x01

/**
 * ID отчета для информации об устройстве
 */
const val INFO_REPORT_ID: Byte = 0x02

/**
 * ID отчета с данными
 */
const val DATA_REPORT_ID: Byte = 0x04

/**
 * Vendor ID, одиннаков для всех модулей
 */
const val VENDOR_ID = 0x483
