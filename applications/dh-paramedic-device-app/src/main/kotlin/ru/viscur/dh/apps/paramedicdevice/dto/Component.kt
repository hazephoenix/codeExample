package ru.viscur.dh.apps.paramedicdevice.dto

/**
 * Created at 30.09.2019 17:28 by TimochkinEA
 */
data class Component(
        val code: List<Code> = listOf(),
        val valueQuantity: ValueQuantity? = null,
        val interpretation: Interpretation? = null
)
