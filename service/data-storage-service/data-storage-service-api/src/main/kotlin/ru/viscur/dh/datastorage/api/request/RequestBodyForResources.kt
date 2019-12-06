package ru.digitalhospital.dhdatastorage.dto

/**
 * Created at 30.09.2019 16:18 by SherbakovaMA
 *
 * Данные для запросов по ресурсам
 * @param filter набор пар для фильтрации: наименование_поля - значение_в_полу. Например, ищем по системе и коду:
 * {
 * "system":"ICD-10",
 * "code":"A00"
 * }
 * если второе значение null, то поиск наименование_поля is null
 * @param filterLike ищет [filter] не по полному совпадению, а частичному. тогда указывается в [filter] наименование_поля - часть_строки_поиска
 * @param orderBy набор наименований полей ресурса, по которым нужно сортировать результат. Например, ["name", "system"]
 * Можно указывать desc: ["name desc", "system desc"]
 * Применится сортировка order by r.resource->>'name' desc, r.resource->>'system' desc
 * Без указания (по умолчанию) сортируется по id
 */
class RequestBodyForResources(
        val filter: Map<String, String?> = mapOf(),
        val filterLike: Boolean = false,
        val orderBy: List<String> = listOf("id")
){
    override fun toString(): String {
        return "{ filter=$filter, orderBy=$orderBy }"
    }
}
