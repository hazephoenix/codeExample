package ru.viscur.dh.integration.doctorapp.rest.security

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UsernameNotFoundException
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.ResourceService.ResourceNotFoundException
import ru.viscur.dh.fhir.model.enums.HumanNameUse
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.security.DhUserDetails

/**
 * AuthenticationProvider для аутентификации через МИС
 */
class MisAuthenticationProvider(
        private val resourceService: ResourceService,
        private val conceptService: ConceptService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication?): Authentication? {
        if (authentication == null) {
            return null
        }
        authentication as UsernamePasswordAuthenticationToken
        val login = authentication.name
        val password = authentication.credentials as String?

        if (password.isNullOrBlank()) {
            throw BadCredentialsException("Bad password");
        }
        // TODO когда будет готов функционал со стороны МИС,
        //      надо тут поддержать
        val practitioner =
                try {
                    resourceService.byId(ResourceType.Practitioner, login)
                } catch (e: ResourceNotFoundException) {
                    throw UsernameNotFoundException("Unknown user");
                }

        val token = UsernamePasswordAuthenticationToken(
                authentication.principal,
                authentication.credentials,
                listOf(SimpleGrantedAuthority("ROLE_MIS_PRACTITIONER"))
        )
        val name =
                practitioner.firstOfficialName ?: practitioner.firstUsualName ?: createEmpty()

        token.details = DhUserDetails(
                practitioner.id,
                login,
                name.text,
                name.family,
                name.given,
                practitioner.qualification.map {
                    val qualification = conceptService.byCodeableConcept(it.code)
                    DhUserDetails.Specialization(
                            qualification.code,
                            qualification.display
                    )
                }
        )
        return token
    }

    private fun createEmpty(): HumanName {
        return HumanName(
                HumanNameUse.official,
                "",
                "",
                listOf(""),
                listOf()
        )
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java
                .isAssignableFrom(authentication)
    }
}