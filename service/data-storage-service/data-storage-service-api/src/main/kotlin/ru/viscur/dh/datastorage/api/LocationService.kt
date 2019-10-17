package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Location

/**
 * Created at 15.10.2019 11:51 by SherbakovaMA
 *
 * Сервис для работы с [Location]
 */
interface LocationService {

    /**
     * По [id]
     */
    fun byId(id: String): Location

    /**
     * Все кабинеты, у которых в lastPatientInfo указан пациент [patientId]
     */
    fun withPatientInLastPatientInfo(patientId: String): List<Location>
}