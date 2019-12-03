package ru.viscur.dh.datastorage.impl.entity

import ru.viscur.dh.datastorage.api.model.call.CallGoal
import ru.viscur.dh.datastorage.api.model.call.CallStatus
import ru.viscur.dh.datastorage.api.model.call.CallableSpecialization
import ru.viscur.dh.fhir.model.enums.Severity
import java.util.*
import javax.persistence.*
import javax.persistence.EnumType.STRING

@Entity
@Table(name = "doctor_call")
class DoctorCall (
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
    @Column(name = "qualification")
    @Enumerated(STRING)
    var qualification: CallableSpecialization = CallableSpecialization.Surgeon,

    /**
     * Врач которого вызывали
     *
     * Ссылка на [ru.digitalhospital.dhdatastorage.dto.Resource.id] с типом
     * [ru.digitalhospital.dhdatastorage.dto.Resource.resourceType] [ru.viscur.dh.fhir.model.enums.ResourceType.Practitioner]
     */
    @Column(name = "doctor_id")
    var doctorId: String = "",

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