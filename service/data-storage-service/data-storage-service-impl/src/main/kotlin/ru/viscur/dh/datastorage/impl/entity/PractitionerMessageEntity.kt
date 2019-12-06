package ru.viscur.dh.datastorage.impl.entity

import ru.viscur.dh.datastorage.api.model.message.PractitionerMessageType
import java.util.*
import javax.persistence.*

/**
 * Сообщения для врача
 */
@Entity
@Table(name = "practitioner_message")
class PractitionerMessageEntity(
        /**
         * ID сообщения (GUID)
         */
        @Id
        val id: String = "",

        /**
         * Дата и время сообщения
         */
        @Column(name = "date_time")
        val dateTime: Date = Date(),

        /**
         * ID врача
         */
        @Column(name = "practitioner_id")
        val practitionerId: String,

        /**
         * ID обследования
         */
        @Column(name = "clinical_impression_id")
        val clinicalImpressionId: String,

        /**
         * Тип сообщения
         */
        @Column(name = "message_type")
        @Enumerated(EnumType.STRING)
        val messageType: PractitionerMessageType,

        /**
         * Сообщение скрыто
         */
        @Column(name = "hidden")
        var hidden: Boolean
)
