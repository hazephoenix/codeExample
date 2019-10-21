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

    /**
     * По типу процедур, обследований: определить однозначно кабинет, в котором проводится определенная процедура
     * Ищет в [LocationExtension.observationType][ru.viscur.dh.fhir.model.type.LocationExtension.observationType]
     * @param type код услуги/процедуры, код из "ValueSet/Observation_types"
     */
    fun byObservationType(type: String): Location
}