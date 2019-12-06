package ru.viscur.dh.practitioner.call.impl.speech

import java.lang.RuntimeException

interface SpeechSynthesisService {
    /**
     * Преобразование текста в голос
     * @throws SpeechSynthesisTemporaryNotAvailableException если сервис генерации речи не доступен
     */
    fun textToSpeech(text: String): Speech


    class SpeechSynthesisTemporaryNotAvailableException(
            message: String?,
            throwable: Throwable?
    ) : RuntimeException(message, throwable)
}