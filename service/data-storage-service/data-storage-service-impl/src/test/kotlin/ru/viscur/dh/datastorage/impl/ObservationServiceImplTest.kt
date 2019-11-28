package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.test.context.*
import ru.viscur.autotests.utils.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.now

@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only")
class ObservationServiceImplTest {
    @Autowired
    lateinit var resourceService: ResourceService
    @Autowired
    lateinit var observationService: ObservationService

    @Test
    @Order(1)
    fun `should return ServiceRequest by observation`() {
        val createdResources = mutableListOf<BaseResource>()
        val patient = Helpers.createPatientResource("103")
                .let { resourceService.create(it) }.also { createdResources.add(it) }
        val practitioner = Helpers.createPractitioner()
                .let { resourceService.create(it) }.also { createdResources.add(it) }
        val servReq = Helpers.createServiceRequestResource("", patient.id)
                .let { resourceService.create(it) }.also { createdResources.add(it) }
        val carePlan = Helpers.createCarePlan(patient.id, listOf(servReq))
                .let { resourceService.create(it) }.also { createdResources.add(it) }

        // Добавляем обследование
        val observation = observationService.create(patient.id,
            Observation(
                    basedOn = Reference(servReq),
                    subject = Reference(patient),
                    performer = listOf(Reference(practitioner)),//кто по факту сделал
                    code = CodeableConcept(
                            code = "HIRURG",
                            systemId = "",
                            display = "Осмотр хирурга"
                    ),
                    valueString = "Слизистые носоглотки без изменений",
                    issued = now()
            ),
            "",
            Severity.GREEN
        )
        createdResources.add(observation as BaseResource)

        // Проверяем, что статус маршрутного листа изменился на "ожидает результатов"
        assertEquals(resourceService.byId(ResourceType.CarePlan, carePlan.id).status, CarePlanStatus.waiting_results)

        // Обследование зарегистрировано
        assertEquals(resourceService.byId(ResourceType.Observation, observation.id).status, ObservationStatus.registered)

        // TODO: убрать, когда заменим метод поиска обследований
        // Поиск обследования по id пациента и статусу обследования
        val createdObservation = observationService.byPatientAndStatus(patient.id, ObservationStatus.registered).firstOrNull()
        assertNotNull(createdObservation)

        // Ждем результатов обследования
        assertEquals(resourceService.byId(ResourceType.ServiceRequest, servReq.id).status, ServiceRequestStatus.waiting_result)

        // Добавляем результаты в обследование и завершаем его
        observation.valueInteger = 180
        observation.status = ObservationStatus.final
        observationService.update(patient.id, observation)

        // Проверяем, что статусы направления и маршрутного листа обновились
        assertEquals(resourceService.byId(ResourceType.ServiceRequest, servReq.id).status, ServiceRequestStatus.completed)
        assertEquals(resourceService.byId(ResourceType.CarePlan, carePlan.id).status, CarePlanStatus.results_are_ready)

        // TODO: finalize deleting test resources
        createdResources.forEach {
            resourceService.deleteById(ResourceType.byId(it.resourceType), it.id)
        }
    }
}