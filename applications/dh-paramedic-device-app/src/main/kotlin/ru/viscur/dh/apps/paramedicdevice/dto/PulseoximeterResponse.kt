package ru.viscur.dh.apps.paramedicdevice.dto

class PulseoximeterResponse(

        /**
         * SpO2
         */
        val spO2: Int,

        /**
         * ЧП
         */
        val pulseRate: Int,

        /**
         * ЧД
         */
        val resp: Int
)