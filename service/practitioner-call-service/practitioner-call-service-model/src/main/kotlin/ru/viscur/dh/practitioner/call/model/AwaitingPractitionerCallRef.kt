package ru.viscur.dh.practitioner.call.model

import java.util.*

/**
 * Ссылка на ожидающий вызов
 *
 * @property callId ID вызова
 * @property dateTime дата и время вызова
 * @property awaitingCallStatusStage стадия ожидания
 * @property voiceCallCount сколько раз вызывали через динамики, заполняется только в случае,
 *          если [awaitingCallStatusStage] == [AwaitingCallStatusStage.VoiceCall]
 */
class AwaitingPractitionerCallRef(
        val callId: String,
        var lastStageDateTime: Date,
        var awaitingCallStatusStage: AwaitingCallStatusStage,
        var voiceCallCount: Int? = null
)