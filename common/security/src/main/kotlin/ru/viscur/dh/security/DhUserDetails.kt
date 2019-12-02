package ru.viscur.dh.security

class DhUserDetails(
        val id: String,
        val login: String,
        val fullName: String,
        val family: String,
        val given: List<String>,
        val specializations: List<Specialization>
) {


    class Specialization(
            val code: String,
            val name: String
    )

}