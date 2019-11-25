package ru.viscur.dh.mis.integration.impl.reception

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.OBSERVATION_IN_OFFICE_101
import ru.viscur.dh.apps.misintegrationtest.util.OBSERVATION_IN_OFFICE_202
import ru.viscur.dh.apps.misintegrationtest.util.ServiceRequestSimple
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.isInspectionOfResp

/**
 * Created at 21.11.2019 14:29 by SherbakovaMA
 *
 * Тест на определение предлагаемого ответственного врача
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Practitioners ids are for tests only")
class ResponsiblePractitionerPredictorTest {

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var patientService: PatientService

    @Autowired
    lateinit var practitionerService: PractitionerService

    @Test
    fun testCompareWorkload() {
        //Проверка того, что отв. предлагает сделать самого "разгруженного" врача
        forTestService.cleanDb()
        val diagnosisCode = "I80.0"
        val gender = Gender.male
        var predictedServRequests = patientService.predictServiceRequests(diagnosisCode, gender.name, listOf())
        var actual = predictedServRequests.entry.filter { it.resource is ServiceRequest }.map { it.resource as ServiceRequest }
                .find { it.isInspectionOfResp() }!!.performer!!.first().id
        Assertions.assertEquals(Helpers.surgeonId, actual)

        val pSr1 = forTestService.registerPatient(
                severity = Severity.RED,
                diagnosisCode = "I80.0",
                servReqs = listOf(
                        ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                        ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
                )
        )
        val pId1 = pSr1.first().subject!!.id!!
        val patientToExamineForSurgeon1 = patientService.patientsToExamine(Helpers.surgeonId)
        forTestService.compareListOfString(listOf(pId1), patientToExamineForSurgeon1.map { it.patientId }, "должен быть один пациент в ответственности у ${Helpers.surgeonId}")

        predictedServRequests = patientService.predictServiceRequests(diagnosisCode, gender.name, listOf())
        actual = predictedServRequests.entry.filter { it.resource is ServiceRequest }.map { it.resource as ServiceRequest }
                .find { it.isInspectionOfResp() }!!.performer!!.first().id
        Assertions.assertEquals(Helpers.surgeon2Id, actual)
    }

    @Test
    fun testIgnoringBlockedPractitioner() {
        //проверка того, что заблокированный врач не предлагается как отв-ый
        forTestService.cleanDb()

        practitionerService.updateBlocked(Helpers.surgeonId, true)

        val diagnosisCode = "I80.0"
        val gender = Gender.male
        val predictedServRequests = patientService.predictServiceRequests(diagnosisCode, gender.name, listOf())
        val actual = predictedServRequests.entry.filter { it.resource is ServiceRequest }.map { it.resource as ServiceRequest }
                .find { it.isInspectionOfResp() }!!.performer!!.first().id
        Assertions.assertEquals(Helpers.surgeon2Id, actual)

        practitionerService.updateBlocked(Helpers.surgeonId, false)
    }
}