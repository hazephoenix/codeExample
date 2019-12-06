package ru.viscur.dh.practitioner.call.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.practitioner.call.api.LoudspeakerFacade

@Service
class LoudspeakerFacadeImpl: LoudspeakerFacade {
    override fun say(location: Location, text: String) {
        // TODO implement me
    }
}