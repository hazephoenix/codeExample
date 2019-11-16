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
     * Все непройденные обследования в маршрутном листе, которые проводит очередь
     * Так, очередь игнорирует непройденные назначения по моче
     * то же самое что и [active] полностью или по кабинету, но с фильтрацией
     */
    fun activeForQueue(patientId: String, officeId: String? = null): List<ServiceRequest>

    /**
     * Все непройденные назначения указанной категории (категория это parentCode типа услуги)
     */
    fun activeByObservationCategory(patientId: String, parentCode: String): List<ServiceRequest>

    /**
     * Добавить направления на обследования
     */
    fun add(patientId: String, serviceRequestList: List<ServiceRequest>): CarePlan

    /**
     * Обновить после выполнения - после создания соотв-щего [observation], который на нем основан
     */
    fun updateStatusByObservation(observation: Observation): ServiceRequest

    /**
     * Отменить назначения пациента в кабинете
     */
    fun cancelServiceRequests(patientId: String, officeId: String): List<ServiceRequest>

    /**
     * Отменить назначение пациента по id назначения
     */
    fun cancelServiceRequest(id: String): ServiceRequest
}