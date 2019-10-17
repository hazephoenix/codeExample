package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.*

interface ObservationService {
    fun create(observation: Observation): Observation?

    fun update(observation: Observation): Observation?

    fun findServiceRequest(observation: Observation): ServiceRequest?
}