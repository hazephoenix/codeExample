package ru.viscur.dh.integration.doctorapp.impl.mapper

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.datastorage.api.model.call.DoctorCall
import ru.viscur.dh.datastorage.api.model.message.DoctorMessage
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.integration.doctorapp.api.model.*
import ru.viscur.dh.integration.mis.api.dto.QueueItemDto

@Component
class DoctorAppMapper {
    fun mapCallStorageToApi(storageValue: DoctorCall): ru.viscur.dh.integration.doctorapp.api.model.DoctorCall {
        return ru.viscur.dh.integration.doctorapp.api.model.DoctorCall(
                id = storageValue.id,
                dateTime = storageValue.dateTime,
                caller = Person(
                        id = storageValue.caller.id,
                        fullName = (storageValue.caller.firstOfficialName
                                ?: storageValue.caller.firstUsualName)?.text
                                ?: ""
                ),
                specialization = storageValue.specialization,
                doctor = CallableDoctor(
                        storageValue.doctor.id,
                        (storageValue.doctor.firstOfficialName ?: storageValue.doctor.firstUsualName)?.text
                                ?: "",
                        specializations = listOf(),//storageValue.doctor.qualification.code.coding.
                        disabled = false
                ),
                goal = storageValue.goal,
                patientSeverity = storageValue.patientSeverity,
                location = mapLocationFhirToApi(storageValue.location),
                comment = storageValue.comment,
                status = storageValue.status,
                timeToArrival = storageValue.timeToArrival
        )
    }

    fun mapLocationFhirToApi(source: ru.viscur.dh.fhir.model.entity.Location) = Location(
            source.id,
            source.officeNumber() ?: source.name
    )

    fun mapPractitionerToPerson(source: Practitioner) =
            Person(
                    id = source.id,
                    fullName = source.fullName
            )

    fun mapPractitionerToCallableDoctor(source: Practitioner) =
            CallableDoctor(
                    source.id,
                    source.fullName,
                    source.qualification
                            .code
                            .coding.mapNotNull {
                        try {
                            CallableSpecialization.valueOf(it.code)
                        } catch (ig: IllegalArgumentException) {
                            null
                        }
                    },
                    false // TODO надо где-то взять
            )


    fun mapQueueOfficeToQueuePatient(it: QueueItemDto): QueuePatient = QueuePatient(
            it.patientId,
            it.onum,
            Severity.valueOf(it.severity),
            it.queueCode,
            it.estDuration
    )

    fun mapMessage(it: DoctorMessage) = Message(
            it.id,
            ClinicalImpression(
                    it.clinicalImpression.id,
                    it.clinicalImpression.extension.queueCode,
                    it.clinicalImpression.extension.severity
            ),
            it.dateTime,
            it.messageType.text,
            it.hidden
    )

}