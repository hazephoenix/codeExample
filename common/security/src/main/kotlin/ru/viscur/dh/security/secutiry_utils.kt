package ru.viscur.dh.security

import org.springframework.security.core.context.SecurityContextHolder


fun currentUserDetails(): DhUserDetails {
    val context = SecurityContextHolder.getContext()
            ?: throw Exception("There is no security context")
    val authentication = context.authentication
            ?: throw Exception("There is no authentication information in the current security context")
    return (authentication.details
            ?: throw Exception("There is no user details in the authentication")) as? DhUserDetails
            ?: throw Exception("Authentication has user details of wrong type");
}