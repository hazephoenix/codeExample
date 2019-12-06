package ru.viscur.dh.practitioner.call.api.cmd

class AcceptPractitionerCallCmd(
        /**
         * ID вызова
         */
        val callId: String,

        /**
         * Время до прибытия (минуты)
         */
        val timeToArrival: Short
)