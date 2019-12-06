package ru.viscur.dh.practitioner.call.api.cmd

class DeclinePractitionerCallCmd(
        val callId: String = "",
        val commandInitiator: CommandInitiator = CommandInitiator.User
)