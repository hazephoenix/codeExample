package ru.viscur.dh.integration.practitioner.app.impl.mapper

import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.model.message.PractitionerMessage
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.integration.mis.api.dto.QueueItemDto
import ru.viscur.dh.integration.practitioner.app.api.model.*
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory
import ru.viscur.dh.practitioner.call.model.PractitionerCall

@Component
class PractitionerAppMapper {

    fun mapPractitionerCallToApp(source: PractitionerCall): PractitionerCallAppDto {
        return PractitionerCallAppDto(
                id = source.id,
                dateTime = source.dateTime,
                caller = PersonAppDto(
                        id = source.caller.id,
                        fullName = (source.caller.firstOfficialName
                                ?: source.caller.firstUsualName)?.text
                                ?: ""
                ),
                specializationCategory = source.specializationCategory,
                practitioner = PractitionerAppDto(
                        source.practitioner.id,
                        (source.practitioner.firstOfficialName ?: source.practitioner.firstUsualName)?.text
                                ?: "",
                        specializationCategories = listOf(),
                        disabled = false
                ),
                goal = source.goal,
                patientSeverity = source.patientSeverity,
                location = mapLocationToApp(source.location),
                comment = source.comment,
                status = source.status,
                timeToArrival = source.timeToArrival
        )
    }

    fun mapLocationToApp(source: ru.viscur.dh.fhir.model.entity.Location) = LocationAppDto(
            source.id,
            source.officeNumber() ?: source.name
    )

    fun mapPractitionerToPerson(source: Practitioner) =
            PersonAppDto(
                    id = source.id,
                    fullName = source.fullName
            )

    fun mapPractitionerToApp(source: Practitioner) =
            PractitionerAppDto(
                    source.id,
                    source.fullName,
                    listOfNotNull(try {
                        CallableSpecializationCategory.byFhirId(source.extension.qualificationCategory)
                    } catch (ig: IllegalArgumentException) {
                        null
                    }),
                    false // TODO надо где-то взять
            )


    fun mapQueueOfficeToQueuePatient(it: QueueItemDto): QueuePatientAppDto = QueuePatientAppDto(
            it.patientId,
            it.onum,
            Severity.valueOf(it.severity),
            it.queueCode,
            it.estDuration
    )

    fun mapMessage(it: PractitionerMessage) = MessageAppDto(
            it.id,
            ClinicalImpressionAppDto(
                    it.clinicalImpression.id,
                    it.clinicalImpression.extension.queueCode,
                    it.clinicalImpression.extension.severity
            ),
            it.dateTime,
            it.messageType.text,
            it.hidden
    )

}