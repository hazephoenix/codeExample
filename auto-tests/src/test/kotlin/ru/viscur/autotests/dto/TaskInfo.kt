package ru.viscur.autotests.dto

data class TaskInfo (
    val id: String,
    val desktopId: String?,
    val type: String,
    val status: String,
    val result: String?,
    val payload: Any? = null
)