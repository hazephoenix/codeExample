package ru.viscur.dh.apps.paramedicdevice.meddevice

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.dto.Identifier
import ru.viscur.dh.apps.paramedicdevice.dto.Observation
import ru.viscur.dh.apps.paramedicdevice.dto.TvesResponse
import ru.viscur.dh.apps.paramedicdevice.dto.ValueQuantity
import java.util.*
import javax.annotation.PostConstruct

/**
 * Created at 11.10.2019 11:36 by TimochkinEA
 *
 * Весы
 */
@Component
class Scale(private val restTemplate: RestTemplate) : IMedDevice {

    private val log: Logger = LoggerFactory.getLogger(Scale::class.java)

    @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
    private lateinit var tvesUrl: String

    @PostConstruct
    fun postConstruct() {
        if (log.isDebugEnabled) {
            log.debug("INIT SCALE DEVICE")
            log.debug("TVES URL: $tvesUrl")
        }
    }

    override fun take(): Observation {
        val response = restTemplate.getForEntity("${tvesUrl}/scale", TvesResponse::class.java)
        return Observation(
                identifier = Identifier(value = UUID.randomUUID().toString()),
                component = listOf(
                        ru.viscur.dh.apps.paramedicdevice.dto.Component(
                                valueQuantity = ValueQuantity(
                                        value = response.body?.value,
                                        unit = response.body?.unit
                                )
                        )
                )
        )
    }
}
