package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

interface ServiceRequestService {
    /**
     * Все назначения в маршрутном листе, упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun all(patientId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе, которые могут быть проведены в заданном кабинете,
     * упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun active(patientId: String, officeId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе,
     * упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun active(patientId: String): List<ServiceRequest>

    /**
     * Добавить направления на обследования
     */
    fun add(patientId: String, serviceRequestList: List<ServiceRequest>): CarePlan

    /**
     * Обновить после выполнения - после создания соотв-щего [observation], который на нем основан
     */
    fun updateStatusByObservation(observation: Observation): ServiceRequest
}