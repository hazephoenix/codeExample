package ru.viscur.dh.datastorage.api.model.call

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.Severity
import java.util.*

class DoctorCall(

        val id: String = "",

        /**
         * Дата и время вызова
         */
        val dateTime: Date,

        /**
         * Кто вызывает
         */
        var caller: Practitioner,

        /**
         * Специализация на момент вызова
         * (у врача может быть несколько специализаций, это специализация которая была выбрана
         * вызывающим врачем)
         */
        var specialization: CallableSpecialization,

        /**
         * Врач которого вызывали
         */
        var doctor: Practitioner,

        /**
         * Цель вызова
         */
        var goal: CallGoal,

        /**
         * Степень тяжести пациента
         */
        var patientSeverity: Severity,

        /**
         * Кабинет (куда вызывают)
         */
        var location: Location,

        /**
         * Комментарий вызывающего
         */
        var comment: String,

        /**
         * Статус вызова
         */
        var status: CallStatus,

        /**
        * Время прибытия
         */
        var timeToArrival: Short?


) {

}