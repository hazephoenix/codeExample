package ru.viscur.dh.practitioner.call.impl.loudspeaker.impl

import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerService
import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerSystem
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.model.PractitionerCall

/**
 * Проигрывает вызов всегда
 */
class AlwaysPlayLoudspeakerService(
        private val loudspeakerSystem: LoudspeakerSystem
) : LoudspeakerService {
    override fun play(call: PractitionerCall, speech: Speech) {
        loudspeakerSystem.play(call, speech)
    }
}