package ru.viscur.dh.security

class DhUserDetails(
        val id: String,
        val login: String,
        val fullName: String,
        val family: String,
        val given: List<String>
) {

}