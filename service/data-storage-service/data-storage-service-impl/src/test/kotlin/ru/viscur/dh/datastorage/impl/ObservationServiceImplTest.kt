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
    @Autowired
    lateinit var serviceRequestService: ServiceRequestService

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
        val clinicalImpression = Helpers.createClinicalImpression(patient.id, Severity.YELLOW, listOf(Reference(carePlan)))
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
        assertEquals(resourceService.byId(ResourceType.CarePlan, carePlan.id).status, CarePlanStatus.active)

        // Обследование зарегистрировано
        assertEquals(resourceService.byId(ResourceType.Observation, observation.id).status, ObservationStatus.registered)

        // Добавляем результаты в обследование и завершаем его
        observation.valueInteger = 180
        observation.status = ObservationStatus.final
        observationService.update(patient.id, observation)
        serviceRequestService.updateStatusByObservation(observation)
        assertEquals(resourceService.byId(ResourceType.ServiceRequest, servReq.id).status, ServiceRequestStatus.completed)

        // TODO: finalize deleting test resources
        createdResources.forEach {
            resourceService.deleteById(ResourceType.byId(it.resourceType), it.id)
        }
    }
}