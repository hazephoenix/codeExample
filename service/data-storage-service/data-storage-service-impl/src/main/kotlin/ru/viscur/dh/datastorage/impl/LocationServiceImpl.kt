package ru.viscur.dh.datastorage.impl

import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.ResourceType

/**
 * Created at 15.10.2019 12:10 by SherbakovaMA
 */
class LocationServiceImpl(
        private val resourceService: ResourceService
) : LocationService {

    override fun byId(id: String): Location? = resourceService.byId(ResourceType.Location, id)
}