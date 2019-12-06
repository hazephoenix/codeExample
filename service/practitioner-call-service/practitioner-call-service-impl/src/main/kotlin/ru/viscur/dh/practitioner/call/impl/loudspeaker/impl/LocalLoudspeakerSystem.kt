package ru.viscur.dh.practitioner.call.impl.loudspeaker.impl

import ru.viscur.dh.practitioner.call.impl.loudspeaker.LoudspeakerSystem
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.model.PractitionerCall
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.LineEvent
import javax.sound.sampled.LineListener

/**
 * Воспроизводит на компе на котором запущена система.
 *
 * По большому счету, используется для отладки
 */
class LocalLoudspeakerSystem : LoudspeakerSystem {
    private val waitObj = Object()
    override fun play(call: PractitionerCall, speech: Speech) {
        val clip = AudioSystem.getClip()
        clip.open(AudioSystem.getAudioInputStream(speech.inputStream))
        clip.addLineListener {
            if (it.type == LineEvent.Type.STOP) {
                synchronized(waitObj) {
                    waitObj.notifyAll()
                }
            }
        }
        clip.start()
        synchronized(waitObj) {
            waitObj.wait()
        }
    }
}