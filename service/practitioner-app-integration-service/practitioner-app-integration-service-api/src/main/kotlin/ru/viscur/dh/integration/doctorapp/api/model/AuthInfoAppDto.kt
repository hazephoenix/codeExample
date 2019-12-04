package ru.viscur.dh.integration.doctorapp.api.model

class AuthInfoAppDto(
        val id: String,
        val login: String,
        val fullName: String,
        val family: String,
        val given: List<String>,
        val specializations: List<String>,
        val callable: Boolean
) {
}