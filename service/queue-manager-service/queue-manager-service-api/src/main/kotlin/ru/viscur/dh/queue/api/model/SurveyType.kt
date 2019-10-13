package ru.viscur.dh.queue.api.model


/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Тип обследования
 *
 * @param id id записи
 * @param name наименование
 */
data class SurveyType(
        // TODO uuid?
        var id: Long = 0L,
        var name: String? = null
) {
    override fun toString(): String {
        return "SurveyType(id=$id, name=$name)"
    }
}
