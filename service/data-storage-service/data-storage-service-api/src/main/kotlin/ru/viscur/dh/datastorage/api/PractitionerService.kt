package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Practitioner

/**
 * Created at 23.10.2019 17:15 by SherbakovaMA
 *
 * Сервис для работы с мед. персоналом
 */
interface PractitionerService {

    /**
     * Мед. работник по [id]
     */
    fun byId(id: String): Practitioner

    /**
     * Все мед работники указанных специальностей
     */
    fun byQualifications(codes: List<String>): List<String>
}