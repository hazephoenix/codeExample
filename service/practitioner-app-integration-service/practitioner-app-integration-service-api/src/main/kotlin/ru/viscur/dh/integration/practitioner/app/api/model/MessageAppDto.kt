package ru.viscur.dh.integration.practitioner.app.api.model

import java.util.*

class MessageAppDto(
        val id: String,
        val clinicalImpression: ClinicalImpressionAppDto,
        val dateTime: Date,
        val text: String,
        val hidden: Boolean


) {
}