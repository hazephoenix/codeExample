package ru.viscur.dh.practitioner.call.model


enum class AwaitingCallStatusStage {

    /**
     * Оповещен (наверно) через мобильное приложение
     */
    DoctorAppCall,

    /**
     * Оповещен через динамики
     */
    VoiceCall
}