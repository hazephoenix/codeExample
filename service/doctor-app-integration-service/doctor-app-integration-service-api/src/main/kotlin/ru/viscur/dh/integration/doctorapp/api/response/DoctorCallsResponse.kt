package ru.viscur.dh.integration.doctorapp.api.response

import ru.viscur.dh.integration.doctorapp.api.model.DoctorCall

class DoctorCallsResponse(
        /**
         * Входящие запросы
         */
        val incomingCalls: List<DoctorCall>,

        /**
         * Исходящие запросы
         */
        val outcomingCalls: List<DoctorCall>
)