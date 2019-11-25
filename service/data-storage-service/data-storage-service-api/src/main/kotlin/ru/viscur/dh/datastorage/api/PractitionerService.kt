package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Practitioner

/**
 * Created at 23.10.2019 17:15 by SherbakovaMA
 *
 * Сервис для работы с мед. персоналом
 */
interface PractitionerService {

    /**
     * Все мед. работники
     * @param withBlocked с заблокированными. иначе только активные/незаблокированные
     */
    fun all(withBlocked: Boolean = false): List<Practitioner>

    /**
     * Создание мед. работника
     */
    fun create(practitioner: Practitioner): Practitioner

    /**
     * Редактирование мед. работника
     */
    fun update(practitioner: Practitioner): Practitioner

    /**
     * Мед. работник по [id]
     */
    fun byId(id: String): Practitioner

    /**
     * Все мед работники указанных специальностей
     * Заблокированные не учитываются
     */
    fun byQualifications(codes: List<String>): List<Practitioner>

    /**
     * Все мед работники указанной специальности
     */
    fun byQualification(code: String): List<Practitioner> = byQualifications(listOf(code))

    /**
     * Обновить значение поля Заблокирован [ru.viscur.dh.fhir.model.type.PractitionerExtension.blocked]
     */
    fun updateBlocked(practitionerId: String, value: Boolean): Practitioner
}