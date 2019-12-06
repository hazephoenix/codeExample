package ru.viscur.dh.datastorage.impl.entity

import javax.persistence.*

/**
 * Соответствие между id метки и id сотрудника
 * @param tagId id метки
 * @param practitionerId id сотрудника
 */
@Entity
@Table(name = "tag")
data class TagEntity(
    @Id
    @Column(name = "tag_id", nullable = false)
    var tagId: String,
    @Column(name = "practitioner_id", nullable = false)
    var practitionerId: String
)
