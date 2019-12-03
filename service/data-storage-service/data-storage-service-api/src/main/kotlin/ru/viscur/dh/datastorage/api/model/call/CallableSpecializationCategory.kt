package ru.viscur.dh.datastorage.api.model.call

import java.lang.IllegalStateException

enum class CallableSpecializationCategory(
        val fhirId: String
) {
    /**
     * Хирург
     */
    Surgeon("Surgeon_category"),

    /**
     * Терапевт
     */
    Therapist("Therapist_category"),

    /**
     * Уролог
     */
    Urologist("Urologist_category"),

    /**
     * Гинеколог
     */
    Gynecologist("Gynecologist_category"),

    /**
     * Невролог
     */
    Neurologist("Neurologist_category");


    companion object {
        fun byFhirId(fhirId: String): CallableSpecializationCategory {
            return values().find { it.fhirId == fhirId }
                    ?: throw IllegalStateException("Can't find CallableSpecializationCategory by fhirId $fhirId");
        }
    }

}
