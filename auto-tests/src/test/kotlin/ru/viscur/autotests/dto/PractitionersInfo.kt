package ru.viscur.autotests.dto

import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.Identifier
import ru.viscur.dh.fhir.model.type.PractitionerExtension
import ru.viscur.dh.fhir.model.type.PractitionerQualification

data class PractitionersInfo (
    val id: String,
    val identifier: List<Identifier>?,
    val resourceType: ResourceType.ResourceTypeId,
    var name: List<HumanName>,
    var gender: Gender,
    var qualification: List<PractitionerQualification>,
    var extension: PractitionerExtension,
    val meta: Meta,
    val fullName: String,
    val firstOfficialName: HumanName
)

data class Meta(
    val versionId: Int
)