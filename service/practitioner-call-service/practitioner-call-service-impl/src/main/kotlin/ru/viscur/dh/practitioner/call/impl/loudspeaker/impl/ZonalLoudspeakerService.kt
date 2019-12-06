package ru.viscur.dh.practitioner.call.impl.loudspeaker.impl

import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerService
import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerSystem
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.model.PractitionerCall

/**
 * Проигрывает только в случае если врач в разрешенной зоне
 */
class ZonalLoudspeakerService(
        private val loudspeakerSystem: LoudspeakerSystem
) : LoudspeakerService {
    override fun play(call: PractitionerCall, speech: Speech) {
        // TODO
        //      1) Найти зону в который на данный момент врач
        //      2) Посмотреть, можем ли мы вызывать врача когда он в этой зоне и если можем, то проиграть вызов
        loudspeakerSystem.play(call, speech)
    }
}