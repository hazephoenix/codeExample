package ru.viscur.dh.practitioner.call.impl.speech.impl

import ru.viscur.dh.practitioner.call.impl.speech.Speech
import java.io.InputStream

class StreamSpeech(override val inputStream: InputStream) : Speech {
}