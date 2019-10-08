package ru.viscur.dh.paramedic.devicemonitor

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.paramedic.devicemonitor.configuration.AppUID

/**
 * Created at 27.09.2019 12:18 by TimochkinEA
 */
@RestController
@RequestMapping("/uid")
class HelloController(private val uid: AppUID) {

    @GetMapping
    fun sayHello() = uid
}
