package ru.viscur.dh.paramedic.devicemonitor.meddevice

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.paramedic.devicemonitor.dto.Identifier
import ru.viscur.dh.paramedic.devicemonitor.dto.Observation
import ru.viscur.dh.paramedic.devicemonitor.dto.TvesResponse
import ru.viscur.dh.paramedic.devicemonitor.dto.ValueQuantity
import java.util.*

/**
 * Created at 11.10.2019 12:48 by TimochkinEA
 *
 * Ростомер
 */
@Component
class Height(
        private val restTemplate: RestTemplate,
        @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
        private val tvesUrl: String
) : IMedDevice {
    override fun take(): Observation {
        restTemplate.postForLocation("${tvesUrl}/height/clear", "")
        restTemplate.postForLocation("${tvesUrl}/height/start", "")
        val res = restTemplate.getForEntity("${tvesUrl}/height", TvesResponse::class.java)
        return Observation(
                identifier = Identifier(UUID.randomUUID().toString()),
                component = listOf(
                        ru.viscur.dh.paramedic.devicemonitor.dto.Component(
                                valueQuantity = ValueQuantity(
                                        value = res.body?.value,
                                        unit = res.body?.unit
                                )
                        )
                )
        )
    }
}
