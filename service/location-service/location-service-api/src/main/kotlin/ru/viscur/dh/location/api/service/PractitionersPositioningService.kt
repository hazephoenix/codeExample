package ru.viscur.dh.location.api.service

/**
 * Сервис для работы с подсистемой определения местоположения врачей
 */
interface PractitionersPositioningService {

    /**
     * Получить список работающих врачей
     * (сотрудник считается работающим если его метка меняла зону в течении времени
     * заданного в [ru.viscur.dh.location.api.LOCATION_ACTUAL_TAGS_DURATION])
     */
    fun actualUserIds(): Collection<String>

    /**
     * Получить список сотрудников находищихся в зоне связанной с помещением [officeId]
     */
    fun listPractitionerIdsByOfficeId(officeId: String): Collection<String>

    /**
     * Получить список помещений, связанных с зоной в которой находится сотрудник [practitionerId]
     */
    fun listOfficeIdsByPractitionerId(practitionerId: String): Collection<String>
}
