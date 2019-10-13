package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 02.10.2019 14:47 by TimochkinEA
 */
data class ServiceRequest(
        override val resourceType: String = "ServiceRequest",
        override val identifier: Identifier = Identifier(),
        val code: Code = Code(),
        val encounter: Reference = Reference(Encounter())
) : Resource
