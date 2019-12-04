package ru.viscur.dh.integration.doctorapp.api.model

import java.util.*

class MessageAppDto(
        val id: String,
        val clinicalImpression: ClinicalImpressionAppDto,
        val dateTime: Date,
        val text: String,
        val hidden: Boolean


) {
}