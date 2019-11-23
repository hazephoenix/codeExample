package ru.viscur.dh.apps.paramedicdevice.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.fhir.model.entity.Patient

/**
 * Формирование данных для маршрутного листа
 */
@Service
class RouteSheetBuilder(
        private val restTemplate: RestTemplate,
        @Value("\${central.server.app.url:http://localhost:8080/}")
        private val serverUrl: String
) {

    fun build(patientId: String): Map<String, String> {
        val patient = restTemplate.getForEntity("${serverUrl}Patient/$patientId", Patient::class.java)
        TODO("Implement me!")
    }
}