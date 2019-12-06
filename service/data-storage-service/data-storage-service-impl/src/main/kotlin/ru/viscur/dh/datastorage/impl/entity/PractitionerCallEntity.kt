package ru.viscur.dh.datastorage.impl.entity

import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.practitioner.call.model.CallGoal
import ru.viscur.dh.practitioner.call.model.CallStatus
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING

@Entity
@Table(name = "practitioner_call")
class PractitionerCallEntity(
        @Id
        val id: String = "",

        /**
         * Дата и время вызова
         */
        @Column(name = "date_time")
        val dateTime: Date = Date(),
        /**
         * Id врача, который вызывает.
         *
         * Ссылка на [ru.digitalhospital.dhdatastorage.dto.Resource.id] с типом
         * [ru.digitalhospital.dhdatastorage.dto.Resource.resourceType] [ru.viscur.dh.fhir.model.enums.ResourceType.Practitioner]
         */
        @Column(name = "caller_id")
        var callerId: String = "",

        /**
         * Специализация на момент вызова
         * (у врача может быть несколько специализаций, это специализация которая была выбрана
         * вызывающим врачем)
         */
        @Column(name = "specialization_category")
        @Enumerated(STRING)
        var specializationCategory: CallableSpecializationCategory = CallableSpecializationCategory.Surgeon,

        /**
         * Врач которого вызывали
         *
         * Ссылка на [ru.digitalhospital.dhdatastorage.dto.Resource.id] с типом
         * [ru.digitalhospital.dhdatastorage.dto.Resource.resourceType] [ru.viscur.dh.fhir.model.enums.ResourceType.Practitioner]
         */
        @Column(name = "practitioner_id")
        var practitionerId: String = "",

        /**
         * Цель вызова
         */
        @Column(name = "goal")
        @Enumerated(STRING)
        var goal: CallGoal = CallGoal.Emergency,

        /**
         * Степень тяжести пациента
         */
        @Column(name = "patient_severity")
        @Enumerated(STRING)
        var patientSeverity: Severity = Severity.RED,

        /**
         * Кабинет (куда вызывают)
         * Ссылка на [ru.digitalhospital.dhdatastorage.dto.Resource.id] с типом
         * [ru.digitalhospital.dhdatastorage.dto.Resource.resourceType] [ru.viscur.dh.fhir.model.enums.ResourceType.Location]
         */
        @Column(name = "location_id")
        var locationId: String = "",

        /**
         * Комментарий вызывающего
         */
        @Column(name = "comment")
        var comment: String = "",

        /**
         * Статус вызова
         */
        @Column(name = "status")
        @Enumerated(STRING)
        var status: CallStatus = CallStatus.Awaiting,

        /**
         * Время прибытия
         */
        @Column(name = "time_to_arrival")
        var timeToArrival: Short? = null
)