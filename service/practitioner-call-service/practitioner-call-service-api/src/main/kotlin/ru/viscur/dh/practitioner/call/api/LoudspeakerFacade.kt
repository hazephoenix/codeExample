package ru.viscur.dh.practitioner.call.api

import ru.viscur.dh.fhir.model.entity.Location

/**
 * Фасад для работы с вызовами через динамики
 */
interface LoudspeakerFacade {

    /**
     * Сделать объявление через динамики.
     *
     * @param location кабинет, в зоне которого необходимо сделать объявление
     * @param text текст объявления
     */
    fun say(location: Location, text: String)

}