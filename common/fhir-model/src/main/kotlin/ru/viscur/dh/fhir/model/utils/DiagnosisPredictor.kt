package ru.viscur.dh.fhir.model.utils

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Предсказатель диагноза в системе МКБ-10 // TODO
 *
 * TODO навверно не в моделе он должен быть?
 */
class DiagnosisPredictor {
    fun predict(): Concept {
        val codes = mapOf(
                "A00" to "Холера",
                "A08.0" to "Ротавирусный энтерит",
                "H74.0" to "Тимпаносклероз",
                "Q43.1" to "Болезнь Гиршпрунга"
        )
        val diagnosisCode = codes.entries.random()
        return Concept(
                code = diagnosisCode.key,
                system = "ValueSet/${ValueSetName.ICD_10.id}",
                display = diagnosisCode.value
        )
    }
}