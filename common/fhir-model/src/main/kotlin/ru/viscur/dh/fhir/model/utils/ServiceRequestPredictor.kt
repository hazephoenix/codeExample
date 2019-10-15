package ru.viscur.dh.fhir.model.utils

//import ru.viscur.dh.fhir.model.controller.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Предсказатель списка необходимых услуг в системе МКБ-10 // TODO
 *
 * TODO навверно не в моделе он должен быть?
 */
class ServiceRequestPredictor {
    fun predict(): Bundle {
        // кабинет, в котором проводится предложенная услуга
        val location = Location(
                identifier = listOf(Identifier(value = "139", type = IdentifierType.OFFICE_NUMBER)),
                name = "Смотровой кабинет"
        )
        TODO("Ошибка компиляции: Unresolved reference: patient. В исходном проекте он в sampleData.kt")
        /*// Предложенная слуга
        val suggestedServiceRequest = ServiceRequest(
                subject = Reference(patient),
                code = CodeableConcept(
                        code = "HIRURG",
                        systemId = ValueSetName.OBSERVATION_TYPES.id,
                        display = "Осмотр хирурга"
                ),
                locationReference = listOf(Reference(location)),
                extension = ServiceRequestExtension(executionOrder = 1)
        )
        return Bundle(
                type = BundleType.BATCH.value,
                entry = listOf(BundleEntry(suggestedServiceRequest))
        )*/
    }
}