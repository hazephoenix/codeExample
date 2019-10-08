package ru.viscur.dh.paramedic.devicemonitor.dto

/**
 * Created at 02.10.2019 17:03 by TimochkinEA
 */
data class Encounter(
        override val resourceType: String = "Encounter",
        override val identifier: Identifier = Identifier()
) : Resource
