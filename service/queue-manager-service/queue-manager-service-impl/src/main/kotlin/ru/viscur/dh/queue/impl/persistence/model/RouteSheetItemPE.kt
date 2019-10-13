package ru.viscur.dh.queue.impl.persistence.model

import javax.persistence.*

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
@Entity
@Table(name = "route_sheet_items")
data class RouteSheetItemPE(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "route_sheet_items_seq")
        @SequenceGenerator(name = "route_sheet_items_seq", sequenceName = "route_sheet_items_seq", allocationSize = 0)
        var id: Long = 0L,
        @ManyToOne(targetEntity = UserPE::class, cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "userId", referencedColumnName = "id")
        var user: UserPE = UserPE(),
        @ManyToOne(targetEntity = SurveyTypePE::class, cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "surveyTypeId", referencedColumnName = "id")
        var surveyType: SurveyTypePE = SurveyTypePE(),
        @Column
        var priority: Double = 0.0,
        @Column
        var visited: Boolean = false,
        @Column
        var onum: Int = 0
) {

    override fun toString(): String {
        return "RouteSheetItem: surveyType=${surveyType.id}, ${surveyType.name}, priority=$priority, visited=$visited"
    }
}
