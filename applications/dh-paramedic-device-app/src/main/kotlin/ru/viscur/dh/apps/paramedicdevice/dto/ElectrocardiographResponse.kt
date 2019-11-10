package ru.viscur.dh.apps.paramedicdevice.dto

class ElectrocardiographResponse(
        /**
         * ЧСС
         */
        val heartRate: Int,

        /**
         * ЭКГ: отведение I (SVG изображение)
         */
        val ecg1: String,

        /**
         * ЭКГ: отведение II (SVG изображение)
         */
        val ecg2: String,

        /**
         * ЭКГ: отведение III (SVG изображение)
         */
        val ecg3: String,

        /**
         * ЧД
         */
        val rsp: Int
) {
}