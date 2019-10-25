package ru.digitalhospital.dhdatastorage.dto

/**
 * Created at 30.09.2019 16:18 by SherbakovaMA
 *
 * Данные для запросов по ресурсам
 * @param filter набор пар для фильтрации: наименование_поля - часть_строки_поиска. Например, ищем по системе и коду:
 * {
 * "system":"ICD-10",
 * "code":"A00"
 * }
 * @param orderBy набор наименований полей ресурса, по которым нужно сортировать результат. Например, ["name", "system"]
 * Можно указывать desc: ["name desc", "system desc"]
 * Применится сортировка order by r.resource->>'name' desc, r.resource->>'system' desc
 * Без указания (по умолчанию) сортируется по id
 */
class RequestBodyForResources(
        val filter: Map<String, String>,
        val orderBy: List<String> = listOf("id")
){
    override fun toString(): String {
        return "{ filter=$filter, orderBy=$orderBy }"
    }
}
