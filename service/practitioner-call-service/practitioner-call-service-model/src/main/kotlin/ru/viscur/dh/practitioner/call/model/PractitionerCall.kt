package ru.viscur.dh.practitioner.call.model

import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.Severity
import java.util.*


class PractitionerCall(

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
        var specializationCategory: CallableSpecializationCategory,

        /**
         * Врач которого вызывали
         */
        var practitioner: Practitioner,

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
)