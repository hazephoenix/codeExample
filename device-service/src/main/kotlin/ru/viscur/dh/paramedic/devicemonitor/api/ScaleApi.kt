package ru.viscur.dh.paramedic.devicemonitor.api

import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import ru.viscur.dh.paramedic.devicemonitor.meddevice.Scale

/**
 * Created at 11.10.2019 11:30 by TimochkinEA
 *
 * API для взаимодействия с весами
 */
@RestController
@RequestMapping("scale")
class ScaleApi(private val scale: Scale) {

    @GetMapping
    fun getMeasurement() = scale.take()
}
