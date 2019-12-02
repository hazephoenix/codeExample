package ru.viscur.dh.location.impl.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.datastorage.impl.service.TagService

@RestController
@RequestMapping("/locations/tags")
class TagsController(
    private val tagService: TagService
) {

    @GetMapping
    fun list() = tagService.findAll()

    @GetMapping("info/{tagId}")
    fun info(@PathVariable tagId: String) = tagService.findById(tagId)

}
