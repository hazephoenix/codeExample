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
     * @param onWorkOnly только те, кто на работе
     * @param onWorkInOfficeId на смене в определенном кабинете
     */
    fun all(withBlocked: Boolean = false, onWorkOnly: Boolean = false, onWorkInOfficeId: String? = null): List<Practitioner>

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
     * Все мед работники указанных категорий специальностей
     * Заблокированные не учитываются
     */
    fun byQualificationCategories(categoryCodes: List<String>): List<Practitioner>

    /**
     * Все мед работники указанной категории специальностей
     */
    fun byQualificationCategory(categoryCode: String): List<Practitioner> = byQualificationCategories(listOf(categoryCode))

    /**
     * Обновить значение поля Заблокирован [ru.viscur.dh.fhir.model.type.PractitionerExtension.blocked]
     */
    fun updateBlocked(practitionerId: String, value: Boolean): Practitioner

    /**
     * Мед. работник заступил на смену/ушел со смены
     * Обновить значения полей
     * [ru.viscur.dh.fhir.model.type.PractitionerExtension.onWork],
     * [ru.viscur.dh.fhir.model.type.PractitionerExtension.onWorkInOfficeId]
     */
    fun updateOnWork(practitionerId: String, value: Boolean, officeId: String? = null): Practitioner
}