package ru.viscur.dh.fhir.model.dto

/**
 * Created at 25.11.2019 9:06 by SherbakovaMA
 *
 * Информация о маршрутном листе для печати
 *
 * @param clinicalImpressionCode маршрутный лист № (то же самое что и № обращения)
 * @param queueCode код в очереди
 * @param severity цвет сорт. потока (степень тяжести)
 * @param name ФИО пациента
 * @param birthDate дата рождения
 * @param age возраст
 * @param entryType канал поступления
 * @param mainSyndrome ведущий синдром
 * @param practitionerName ФИО врача приемного отделения
 * @param transportation траспортировка
 * @param locations информация о кабинетах
 */
data class CarePlanToPrintDto(
        val clinicalImpressionCode: String?,
        val queueCode: String,
        val severity: String,
        val name: String,
        val birthDate: String,
        val age: Int,
        val entryType: String,
        val mainSyndrome: String,
        val practitionerName: String,
        val transportation: String,
        val locations: List<CarePlanToPrintLocationDto>
)

/**
 * Created at 25.11.2019 9:06 by SherbakovaMA
 *
 * Информация о кабинете в маршрутном листе
 *
 * @param onum № п/п (с 1)
 * @param location № кабинета / информация о зоне
 * @param address адрес (какой этаж)
 */
data class CarePlanToPrintLocationDto(
        val onum: Int,
        val location: String,
        val address: String
)
