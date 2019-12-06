package ru.viscur.dh.datastorage.api.model.message

import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.Practitioner
import java.util.*

class PractitionerMessage(
        /**
         * ID сообщения (GUID)
         */
        val id: String,

        /**
         * Дата и время сообщения
         */
        val dateTime: Date,

        /**
         * Врач (кому предназначалось сообщение)
         */
        val practitioner: Practitioner,

        /**
         * Обследование к которому относится сообщение
         */
        val clinicalImpression: ClinicalImpression,

        /**
         * Тим сообщения
         */
        val messageType: PractitionerMessageType,

        /**
         * Сообщение скрытое
         */
        var hidden: Boolean
)