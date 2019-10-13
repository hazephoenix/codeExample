package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 30.09.2019 11:40 by TimochkinEA
 */
data class Observation(
        override val resourceType: String = "Observation",
        override val identifier: Identifier,
        val component: List<Component>
) : Resource
