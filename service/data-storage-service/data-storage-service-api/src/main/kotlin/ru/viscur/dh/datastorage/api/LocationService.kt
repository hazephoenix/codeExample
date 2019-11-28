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
     * Все кабинеты, у которых в [ru.viscur.dh.fhir.model.type.LocationExtension.nextOfficeForPatientsInfo] указан пациент [patientId]
     */
    fun withPatientInNextOfficeForPatientsInfo(patientId: String): List<Location>

    /**
     * С устаревшей информацией [ru.viscur.dh.fhir.model.type.LocationExtensionNextOfficeForPatientInfo]
     */
    fun withOldNextOfficeForPatientsInfo(): List<Location>

    /**
     * По типу процедур, обследований: определить НЕЗАКРЫТЫЕ кабинеты, в которых проводится определенная процедура
     * Ищет в [LocationExtension.observationType][ru.viscur.dh.fhir.model.type.LocationExtension.observationType]
     * @param type код услуги/процедуры, код из "ValueSet/Observation_types"
     */
    fun byObservationType(type: String): List<String>

    /**
     * По типу места/кабинета [Location.type]
     */
    fun byLocationType(type: String): List<Location>

    /**
     * Все кабинеты
     */
    fun allLocations(): List<Location>
}