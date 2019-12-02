package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.TagEntity

@Repository
interface TagRepository : CrudRepository<TagEntity, String> {
    fun findByPractitionerId(practitionerId: String): List<TagEntity>
}
