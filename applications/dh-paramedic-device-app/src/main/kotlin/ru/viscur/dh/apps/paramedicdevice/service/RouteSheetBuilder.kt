package ru.viscur.dh.apps.paramedicdevice.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.fhir.model.dto.CarePlanToPrintDto
import java.util.*

/**
 * Формирование данных для маршрутного листа
 */
@Service
class RouteSheetBuilder(
        private val restTemplate: RestTemplate,
        @Value("\${central.server.app.url:http://localhost:8080/}")
        private val serverUrl: String,
        @Value("\${central.server.app.user:test}")
        private val user: String,
        @Value("\${central.server.app.password:testGGhdJpldczxcnasw8745}")
        private val password: String
) {

    private val log: Logger = LoggerFactory.getLogger(RouteSheetBuilder::class.java)

    fun build(patientId: String): Map<String, String> {
        if (log.isDebugEnabled) {
            log.debug("Get route data for patientId[$patientId]")
        }
        val route = restTemplate.exchange(
                "${serverUrl}report/carePlan",
                HttpMethod.GET, createHeaders(),
                CarePlanToPrintDto::class.java,
                mapOf("patientId" to patientId)
        ).body!!
        if (log.isDebugEnabled) {
            log.debug("route data: $route" )
        }
        val result =  mutableMapOf(
                "num" to route.clinicalImpressionCode!!,
                "cod" to route.queueCode,
                "color" to route.severity,
                "name" to route.name,
                "birthDate" to route.birthDate,
                "age" to route.age.toString(),
                "chanel" to route.entryType,
                "syndrome" to route.mainSyndrome,
                "transportation" to route.transportation,
                "paramedic" to route.practitionerName
        )

        route.locations.forEach { loc ->
            if (loc.location.toIntOrNull() != null) {
                result["l${loc.location}"] = loc.onum.toString()
            } else {
                result["zone"] = loc.location
                result["zoneNum"] = loc.onum.toString()
            }
        }
        return result.toMap()
    }

    private fun createHeaders(): HttpEntity<HttpHeaders> {
        return HttpEntity(
                HttpHeaders().apply {
                    val basic = Base64.getEncoder().encodeToString("$user:$password".toByteArray(Charsets.UTF_8))
                    set("Authorization", "Basic $basic")
                }
        )
    }
}