package ru.viscur.dh.fhir.model.dto

import ru.viscur.dh.fhir.model.entity.*

/**
 * Ответ на запрос определения степени тяжести пациента,
 * используется в [ru.viscur.dh.fhir.model.utils.PatientClassifier]
 *
 * @param severity Степень тяжести пациента
 * @param mainSyndrome Ведущий синдром
 * @param severityReason Причина присвоения данной степени тяжести
 */
data class SeverityResponse(
        var severity: Concept,
        var mainSyndrome: String,
        var severityReason: String
)