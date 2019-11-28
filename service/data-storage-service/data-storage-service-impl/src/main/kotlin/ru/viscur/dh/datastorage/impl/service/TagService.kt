package ru.viscur.dh.datastorage.impl.service

import ru.viscur.dh.datastorage.api.model.Tag

interface TagService {
    fun findAll(): List<Tag>
    fun findById(tagId: String): Tag
    fun create(tagId: String, practitionerId: String): Tag
    fun update(tagId: String, practitionerId: String)
    fun delete(tagId: String)
    fun findAllById(ids: Iterable<String>): List<Tag>
    fun findByPractitionerId(practitionerId: String): List<Tag>
}
