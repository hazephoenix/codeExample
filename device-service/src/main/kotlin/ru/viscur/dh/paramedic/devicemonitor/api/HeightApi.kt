package ru.viscur.dh.paramedic.devicemonitor.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.paramedic.devicemonitor.meddevice.Height

/**
 * Created at 11.10.2019 12:27 by TimochkinEA
 *
 * API для ростомера
 */
@RestController
@RequestMapping("height")
class HeightApi(private val height: Height) {

    @GetMapping
    fun measurement() = height.take()
}
