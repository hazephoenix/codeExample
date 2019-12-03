package ru.viscur.dh.integration.doctorapp.api.model

import ru.viscur.dh.fhir.model.enums.Severity

class ClinicalImpression(
        val id: String,
        val code: String,
        val severity: Severity
) {

}