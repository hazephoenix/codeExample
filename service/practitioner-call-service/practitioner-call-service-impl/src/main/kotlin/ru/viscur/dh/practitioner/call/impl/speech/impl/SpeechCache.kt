package ru.viscur.dh.practitioner.call.impl.speech.impl

import org.springframework.stereotype.Component
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import java.util.zip.CRC32

@Component
class SpeechCache {
    fun createKey(text: String): String {
        val crc = CRC32()
        val bytes = text.toByteArray(Charsets.UTF_8)
        crc.update(bytes)
        return crc.value.toString()
    }

    fun getSpeech(cacheKey: String): Speech? {
        return null
    }

    fun putSpeech(speechCacheKey: String, speech: Speech) {

    }
}