package ru.viscur.dh.practitioner.call.model

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
        fun byFhirIdOpt(fhirId: String): CallableSpecializationCategory? {
            return values().find { it.fhirId == fhirId }
        }

        fun byFhirId(fhirId: String): CallableSpecializationCategory? {
            return byFhirIdOpt(fhirId)
                    ?: throw IllegalStateException("Can't find CallableSpecializationCategory by fhirId $fhirId");
        }

        fun hasFhirId(fhirId: String): Boolean {
            return byFhirIdOpt(fhirId) != null
        }

    }

}
