package ru.viscur.autotests.dto

open class QueueReportInfo(
        val officeId: String?,
        val queueSize: Int,
        val queueWaitingSum: Int,
        val queueWorkload: Int,
        val items: List<Item>
)

data class Item(
        val onum: Int,
        val severity: String,
        val severityDisplay: String,
        val name: String,
        val age: Int,
        val estDuration: String,
        val queueCode: String,
        val patientId: String,
        val status: String
)
