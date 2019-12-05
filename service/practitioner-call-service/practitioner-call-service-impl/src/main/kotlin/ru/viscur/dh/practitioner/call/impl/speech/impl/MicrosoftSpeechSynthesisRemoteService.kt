package ru.viscur.dh.practitioner.call.impl.speech.impl

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.client.ClientHttpRequestInitializer
import org.springframework.http.converter.ByteArrayHttpMessageConverter
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.practitioner.call.impl.speech.Speech
import ru.viscur.dh.practitioner.call.impl.speech.SpeechSynthesisService
import java.io.ByteArrayInputStream

/**
 * Реализация [SpeechSynthesisService] которая использует [https://gitlab.com/digital-hospital/speech-synthesis-service] для синтеза речи
 */
@Service
class MicrosoftSpeechSynthesisRemoteService(
        val speechCache: SpeechCache,
        @Value("\${ru.viscur.dh.microsoft-speech-synthesis-remote.url:http://localhost:8088}")
        val microsoftSpeechSynthesisRemoteUrl: String,
        @Value("\${ru.viscur.dh.microsoft-speech-synthesis-remote.user:DhCentralServer}")
        val microsoftSpeechSynthesisRemoteUser: String,
        @Value("\${ru.viscur.dh.microsoft-speech-synthesis-remote.password:JjIusdmMU;ZsdsPOOdsADcZZ23\$))((}")
        val microsoftSpeechSynthesisRemotePassword: String
) : SpeechSynthesisService {


    override fun textToSpeech(text: String): Speech {
        val speechCacheKey = speechCache.createKey(text)
        var speech = speechCache.getSpeech(speechCacheKey)
        if (speech != null) {
            return speech
        }
        speech = synthesize(text)
        speechCache.putSpeech(speechCacheKey, speech)
        return speech
    }

    private fun synthesize(text: String): Speech {
        val template = RestTemplate(listOf(ByteArrayHttpMessageConverter()))
        template
                .clientHttpRequestInitializers
                .add(ClientHttpRequestInitializer {
                    it.headers.setBasicAuth(
                            microsoftSpeechSynthesisRemoteUser,
                            microsoftSpeechSynthesisRemotePassword,
                            Charsets.UTF_8
                    )
                })
        val wave = template.getForEntity("$microsoftSpeechSynthesisRemoteUrl/speech-synthesis?text=$text", ByteArray::class.java)
        return StreamSpeech(
                ByteArrayInputStream(wave.body)
        )
    }
}