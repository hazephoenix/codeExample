package ru.viscur.dh.practitioner.call.impl.loudspeaker.impl

import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerService
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.model.PractitionerCall

class NopLoudspeakerService : LoudspeakerService {
    override fun play(call: PractitionerCall, speech: Speech) {
        /* nothing to do */
    }
}