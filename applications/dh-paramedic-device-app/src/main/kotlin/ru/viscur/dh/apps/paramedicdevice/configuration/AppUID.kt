package ru.viscur.dh.apps.paramedicdevice.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

/**
 * Created at 01.10.2019 12:39 by TimochkinEA
 *
 * UID приложения
 */
@Component
class AppUID(
        @Value("\${uid:0}")
        val uid: String,
        @Value("\${api.password:0}")
        val apiPassword: String
) {

    @PostConstruct
    fun init() {
        check("0" != uid) { "Program UID is required!" }
        check("0" != apiPassword) { "API pass not defined!" }
    }

    override fun toString(): String = "{uid: $uid}"
}
