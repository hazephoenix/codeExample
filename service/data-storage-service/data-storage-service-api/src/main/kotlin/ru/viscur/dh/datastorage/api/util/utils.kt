package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.qualificationCode

/**
 * Created at 16.11.2019 17:02 by SherbakovaMA
 *
 * Вспомогательные функции
 */

/**
 * Обследования, которые проводит очередь
 * Так, очередь игнорирует непройденные назначения по моче
 */
fun List<ServiceRequest>.filterForQueue(): List<ServiceRequest> {
    return this.filter { it.code.code() !in URINE_ANALYSIS_TYPES }
}

/**
 * Все id кабинетов/мест в группе/секторе указанного кабинета/места
 */
fun allLocationIdsInGroup(locationId: String) = LOCATION_GROUPS.find { it.contains(locationId) }?:
        throw Exception("not found location group for location with id '$locationId'")

/**
 * Врач проводит осмотры (имеет соответствующую специальность)
 */
fun Practitioner.isInspectionQualification() = this.qualificationCode() in INSPECTION_QUALIFICATIONS

/**
 * Является ли назначение осмотром специалиста. Иначе - обследование
 */
fun ServiceRequest.isInspection() = this.code.code() in INSPECTION_TYPES