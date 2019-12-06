package ru.viscur.dh.datastorage.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.model.Tag
import ru.viscur.dh.datastorage.impl.entity.TagEntity
import ru.viscur.dh.datastorage.impl.repository.TagRepository
import ru.viscur.dh.datastorage.impl.service.TagService

@Service
class TagServiceImpl(private val tagRepository: TagRepository) : TagService {
    companion object {
        private val log = LoggerFactory.getLogger(TagServiceImpl::class.java)
    }

    override fun findAll(): List<Tag> = tagRepository.findAll().map(TagEntity::tag)

    override fun findAllById(ids: Iterable<String>) = tagRepository.findAllById(ids).map(TagEntity::tag)

    override fun findById(tagId: String): Tag = tagRepository.findById(tagId).get().tag()

    override fun findByPractitionerId(practitionerId: String): List<Tag> =
            tagRepository.findByPractitionerId(practitionerId).map(TagEntity::tag)

    override fun create(tagId: String, practitionerId: String): Tag =
            tagRepository.save(TagEntity(tagId, practitionerId)).tag()

    override fun update(tagId: String, practitionerId: String) {
        val tag = tagRepository.findById(tagId).orElseThrow { IllegalStateException("Tag not found") }
        tag.practitionerId = practitionerId
        tagRepository.save(tag)
    }

    override fun delete(tagId: String) {
        val tag = tagRepository.findById(tagId).orElseThrow { IllegalStateException("Tag not found") }
        tagRepository.delete(tag)
    }
}

