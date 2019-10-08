package ru.viscur.dh.paramedic.devicemonitor.dto

/**
 * Created at 30.09.2019 17:34 by TimochkinEA
 */
data class Interpretation(
        val coding: List<Coding> = listOf(),
        val text: String = ""
)
