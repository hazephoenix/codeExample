package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*

/**
 * Классификатор, определяющий степень тяжести пациента [Severity]
 */
interface PatientClassifier {

    /**
     * Определить степень тяжести пациента
     *
     * @param bundle Контейнер с входными измерениями и опросником
     * @param takeSyndromes Сколько нужно отобразить предполагаемых ведущих синдромов
     * @return [Severity] Степень тяжести пациента
     */
    fun classify(bundle: Bundle, takeSyndromes: Int): SeverityResponse
}