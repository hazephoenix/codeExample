package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.test.context.*
import ru.viscur.autotests.utils.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.*
import ru.viscur.dh.datastorage.impl.utils.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*

@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DiagnosisPredictorImplTest {
    @Autowired
    lateinit var resourceService: ResourceService
    @Autowired
    lateinit var diagnosisPredictorImpl: DiagnosisPredictorImpl
    lateinit var patient: Patient
    private var createdResources: MutableList<BaseResource> = mutableListOf()

    @BeforeAll
    internal fun createResources() {
        patient = Helpers.createPatientResource(enp = "101").let { resourceService.create(it) }.also { createdResources.add(it) }
        val servReq = Helpers.createServiceRequestResource("Осмотр хирурга", patient.id)
                .let { resourceService.create(it) }.also { createdResources.add(it) }
        val carePlan = Helpers.createCarePlan(patient.id, listOf(servReq))
                .let { resourceService.create(it) }.also { createdResources.add(it) }
        val questionnaireResponse = Helpers.createQuestResponseResource(Severity.YELLOW.name, patient.id)
                .let { resourceService.create(it) }.also { createdResources.add(it) }
       Helpers.createClinicalImpression(patient.id, Severity.YELLOW, listOf(Reference(carePlan), Reference(questionnaireResponse)))
                .let { resourceService.create(it) }.also { createdResources.add(it) }
    }

    @Test
    fun `should update icd to complaints dictionary and save training sample` () {
        val diagnosticReport = Helpers.createDiagnosticReportResource(
                diagnosisCode = "G13",
                patientId = patient.id,
                status = DiagnosticReportStatus.final
                )
            .let { resourceService.create(it) }.also { createdResources.add(it) }

        diagnosisPredictorImpl.saveTrainingSample(diagnosticReport)
    }

    @Test
    fun `should return predicted diagnoses` () {
        val diagnoses = diagnosisPredictorImpl.predict(Helpers.bundle("102", Severity.GREEN.name, listOf()), 5)
        assertNotNull(diagnoses)
    }

    @AfterAll
    fun deleteCreated() {
        createdResources.forEach {
            resourceService.deleteById(ResourceType.byId(it.resourceType), it.id)
        }
    }
}