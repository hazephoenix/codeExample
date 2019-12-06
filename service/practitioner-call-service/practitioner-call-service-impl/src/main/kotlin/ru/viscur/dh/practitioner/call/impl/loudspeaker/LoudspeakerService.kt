package ru.viscur.dh.practitioner.call.impl.loudspeaker

import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.model.PractitionerCall

interface LoudspeakerService {
    fun play(call: PractitionerCall, speech: Speech)
}