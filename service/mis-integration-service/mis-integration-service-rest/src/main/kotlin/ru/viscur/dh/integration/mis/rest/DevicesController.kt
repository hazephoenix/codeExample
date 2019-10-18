package ru.viscur.dh.integration.mis.rest

import org.springframework.http.*
import org.springframework.http.ResponseEntity.*
import org.springframework.web.bind.annotation.*
import ru.viscur.dh.integration.mis.rest.enums.*

/**
 * Контроллер для обработки запросов подсистемы "АРМ Фельдшер"
 */
@RestController
@RequestMapping("/devices")
class DevicesController {

    /**
     * Включить прибор и начать измерение
     */
    @GetMapping("/{device}/start")
    fun startDevice(@PathVariable device: Devices): ResponseEntity<Any> =
            ok().body(mapOf("message" to "$device measurement started"))
}