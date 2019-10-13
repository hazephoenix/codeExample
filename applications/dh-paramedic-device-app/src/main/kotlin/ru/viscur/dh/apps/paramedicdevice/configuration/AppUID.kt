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
        val uid: String
) {

    @PostConstruct
    fun init() {
        check("0" != uid) { "Program UID is required!" }
    }

    override fun toString(): String = "{uid: $uid}"
}
