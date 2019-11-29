package ru.viscur.dh.integration.doctorapp.api.model

import java.util.*

class Message(
        val id: String,
        val clinicalImpression: ClinicalImpression,
        val dateTime: Date,
        val text: String,
        val hidden: Boolean


) {
}