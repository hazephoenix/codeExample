package ru.digitalhospital.dhdatastorage.dto

/**
 * Created at 30.09.2019 16:18 by SherbakovaMA
 *
 * Данные для запросов по одному ресурсу
 * @param resource данные ресурса в формате json.
 *  Например, "{"\"resourceType\": \"Patient\", \"name\": [{\"family\": \"Иванов\", \"given\": [\"Петр\", \"Алексеевич\"]}]}"
 */
class RequestBodyForResource(
        val resource: String
)
