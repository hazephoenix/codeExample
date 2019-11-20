package ru.viscur.autotests.dto

class PractitionerWorkloadInfo(
        officeId: String,
        queueSize: Int,
        queueWaitingSum: Int,
        queueWorkload: Int,
        items: List<Item>,
        val practitioner: PractitionerInfo? = null

):QueueReportInfo(
        officeId = officeId,
        queueSize = queueSize,
        queueWaitingSum = queueWaitingSum,
        queueWorkload = queueWorkload,
        items = items
)

data class PractitionerInfo(
        val practitionerId: String,
        val name: String
)