package ru.viscur.dh.apps.paramedicdevice.api

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import ru.viscur.dh.apps.paramedicdevice.meddevice.Height

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
