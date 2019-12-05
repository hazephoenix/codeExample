package ru.viscur.dh.practitioner.call.impl.speech

interface SpeechSynthesisService {
    fun textToSpeech(text: String): Speech
}