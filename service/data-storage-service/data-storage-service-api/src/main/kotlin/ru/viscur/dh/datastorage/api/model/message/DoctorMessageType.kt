package ru.viscur.dh.datastorage.api.model.message

enum class DoctorMessageType(
        val text: String
) {
    /**
     * Новый пациент в очереди
     */
    NewPatient("Назначен новый пациент"),

    /**
     * Готовы результаты обследования
     */
    ObservationReady("Готовы результаты обследования"),

    /**
     * Превышено время обслуживания пациента
     */
    ServiceTimeElapsed("Превышено время обслуживания пациента")

}