package ru.viscur.dh.integration.doctorapp.rest

import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.model.AuthInfo
import ru.viscur.dh.security.DhUserDetails
import java.lang.Exception

@RestController
@RequestMapping("/integration/doctor-app/auth")
class AuthController(
        val doctorAppService: DoctorAppService
) {

    /**
     * Возвращает информацию об аутентификации текущего пользователя.
     *
     * При логине, клиент делает запрос на данный метод с Basic аутентификацией.
     * Непосредсвенно аутентификацией занимается [ru.viscur.dh.integration.doctorapp.rest.security.MisAuthenticationProvider]
     * и Spring Security. Настроено все в [ru.viscur.dh.integration.doctorapp.rest.config.DoctorAppRestConfig].
     *
     *  + Если аутентификация прошла успешно, запрос приходит в данный метод, данный метод возвращает
     *   [AuthInfo] который показывает что пользователь прошел аутентификацию.
     *  + Если аутентификация не прошла, то Spring Security вернет 401
     */
    @GetMapping("/auth-info")
    fun authInfo(): AuthInfo {
        val context = SecurityContextHolder.getContext()
        val authentication = context.authentication;
        if (isAuthenticated(authentication)) {
            return authenticated(authentication)
        }
        // На случай, если что-то сломалось в конфиге
        throw Exception("User isn't authenticated");
    }

    private fun isAuthenticated(authentication: Authentication?): Boolean {
        return authentication != null &&
                "anonymousUser" != authentication.name &&
                authentication.details is DhUserDetails
    }

    private fun authenticated(authentication: Authentication): AuthInfo {
        val details = authentication.details as DhUserDetails
        return AuthInfo(
                details.id, details.login,
                details.fullName, details.family, details.given, listOf(), false
        )
    }

}