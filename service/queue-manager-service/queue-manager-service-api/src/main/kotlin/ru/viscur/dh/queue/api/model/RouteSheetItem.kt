package ru.viscur.dh.queue.api.model

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Элемент в маршрутных листах: обследование и сопутствующая информация
 *
 * @param id id записи
 * @param user пациент, которому принадлежит маршрутный лист
 * @param surveyType тип обследования
 * @param priority приоритет обследования. если какие-то обследования имеют приоритет выше, то пока их пациент не пройдет не попадет к остальным
 * @param visited прошел ли обследование
 * @param onum номер п/п в листе
 */
data class RouteSheetItem(
        // TODO uuid?
        var id: Long = 0L,
        var user: User = User(),
        var surveyType: SurveyType = SurveyType(),
        var priority: Double = 0.0,
        var visited: Boolean = false,
        var onum: Int = 0
) {

    override fun toString(): String {
        return "RouteSheetItem: surveyType=${surveyType.id}, ${surveyType.name}, priority=$priority, visited=$visited"
    }
}
