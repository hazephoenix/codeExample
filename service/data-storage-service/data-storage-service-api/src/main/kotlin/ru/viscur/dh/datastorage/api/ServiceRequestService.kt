package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.type.*

interface ServiceRequestService {
    /**
     * Все назначения в маршрутном листе, упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun getAll(patientId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе в определенном кабинете,
     * упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun getActive(patientId: String, officeId: String): List<ServiceRequest>

    /**
     * Все непройденные назначения в маршрутном листе,
     * упорядочены по [executionOrder][ru.viscur.dh.fhir.model.type.ServiceRequestExtension.executionOrder]
     */
    fun getActive(patientId: String): List<ServiceRequest>

    /**
     * Cоздать назначение для врача (по коду специальности)
     */
    fun createForPractitioner(practitionerRef: Reference): ServiceRequest
}