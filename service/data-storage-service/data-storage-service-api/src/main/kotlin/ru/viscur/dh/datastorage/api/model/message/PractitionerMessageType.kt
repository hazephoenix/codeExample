package ru.viscur.dh.datastorage.api.model.message

enum class PractitionerMessageType(
        val text: String
) {
    /**
     * Новый пациент в очереди
     */
    NewPatient("Назначен новый пациент"),

    /**
     * Готовы результаты обследования
     */
    ObservationsReady("Готовы результаты обследования"),

    /**
     * Превышено время обслуживания пациента
     */
    ServiceTimeElapsed("Превышено время обслуживания пациента")

}