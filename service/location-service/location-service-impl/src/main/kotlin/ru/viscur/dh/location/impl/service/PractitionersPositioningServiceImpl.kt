package ru.viscur.dh.location.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.model.Tag
import ru.viscur.dh.datastorage.impl.service.TagService
import ru.viscur.dh.datastorage.impl.service.ZoneService
import ru.viscur.dh.location.api.service.PractitionersPositioningService

@Service
class PractitionersPositioningServiceImpl(
    private val positions: Positions,
    private val tagService: TagService,
    private val zoneService: ZoneService,
    private val rfidLocationService: RfidLocationService
) : PractitionersPositioningService {

    override fun actualUserIds(): Collection<String> =
        tagService.findAllById(positions.getActual()).map(Tag::practitionerId).toSet()

    override fun listPractitionerIdsByOfficeId(officeId: String): Collection<String> {
        zoneService.findAll().find { officeId in it.officeIds }?.let { zone ->
            val tagIds = rfidLocationService.listZoneTags(zone.zoneId).toSet()
            val tags = tagService.findAllById(tagIds)
            return tags.map { it.practitionerId }
        }
        return emptyList()
    }

    override fun listOfficeIdsByPractitionerId(practitionerId: String): Collection<String> = tagService.findAll()
        .find { it.practitionerId == practitionerId }
        ?.tagId
        ?.let { tagId: String -> positions[tagId] }
        ?.let { zoneId: String -> zoneService.findById(zoneId) }
        ?.officeIds
        ?: emptyList()
}
