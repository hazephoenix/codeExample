package ru.digitalhospital.queueManager.entities

import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Тип обследования
 *
 * @param id id записи
 * @param name наименование
 */
@Entity
@Table(name = "survey_types")
data class SurveyType(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "survey_types_seq")
        @SequenceGenerator(name = "survey_types_seq", sequenceName = "survey_types_seq", allocationSize = 0)
        var id: Long = 0L,
        @Column
        var name: String? = null
) {
    override fun toString(): String {
        return "SurveyType(id=$id, name=$name)"
    }
}
