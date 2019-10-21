package ru.viscur.dh.fhir.model.utils

import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*

class ResponsibleSpecialistPredictor {
    fun predict(conceptId: String): ListResource =
        ListResource(
            title = "Предлагаемый список ответсвенных врачей",
            entry = listOf(
                ListResourceEntry(
                    Reference(
                        reference = "Practitioner/05a45df1-a9fb-4c7a-8ae9-b1b593e84aa2",
                        type = ResourceType.ResourceTypeId.Practitioner
                    )
                ),
                ListResourceEntry(
                    Reference(
                        reference = "Practitioner/cd84b546-0e36-41c2-b7fd-ca8ff94371a9",
                        type = ResourceType.ResourceTypeId.Practitioner
                    )
                )
            )
        )
}