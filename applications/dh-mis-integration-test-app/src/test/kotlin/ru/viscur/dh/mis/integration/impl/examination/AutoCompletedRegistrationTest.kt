package ru.viscur.dh.mis.integration.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.apps.misintegrationtest.util.*
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.ServiceRequestService
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ServiceRequestStatus
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.identifierValue
import ru.viscur.dh.fhir.model.utils.identifierValueNullable
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import ru.viscur.dh.integration.mis.api.ReceptionService
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.queue.api.QueueManagerService

/**
 * Created at 09.11.2019 10:50 by SherbakovaMA
 *
 * Тест на авто прохождение услуги [REGISTERING] (проставляется статус = completed), если она есть в маршрутном листе
 * Observation не создается
 * Порядок ей не ставится, кабинет тоже, в распечатке маршрутного листе не участвует
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db")
class AutoCompletedRegistrationTest {

    @Autowired
    lateinit var queueManagerService: QueueManagerService

    @Autowired
    lateinit var reportService: ReportService

    @Autowired
    lateinit var locationService: LocationService

    @Autowired
    lateinit var conceptService: ConceptService

    @Autowired
    lateinit var serviceRequestService: ServiceRequestService

    @Autowired
    lateinit var forTestService: ForTestService

    @Test
    fun test() {
        forTestService.cleanDb()
        forTestService.updateOfficeStatuses()
        queueManagerService.officeIsClosed(OFFICE_119)//закрываем 2й кабинет рентгена

        //проверяемые действия
        val checkSr = forTestService.registerPatient(servReqs = listOf(
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_101),
                ServiceRequestSimple(REGISTERING),
                ServiceRequestSimple(OBSERVATION2_IN_OFFICE_101),
                ServiceRequestSimple(OBSERVATION_IN_OFFICE_202)
        ))
        val checkP = checkSr.first().subject!!.id()

        val officeId = OFFICE_101

        //проверка после
        forTestService.checkQueueItems(listOf(QueueOfOfficeSimple(officeId = officeId, officeStatus = LocationStatus.BUSY, items = listOf(
                QueueItemSimple(patientId = checkP)
        ))))
        val expServReq = listOf(
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION2_IN_OFFICE_101, locationId = OFFICE_101),
                ServiceRequestSimple(code = OBSERVATION_IN_OFFICE_202, locationId = OFFICE_202),
                ServiceRequestSimple(code = OBSERVATION_OF_SURGEON, locationId = GREEN_ZONE)
        )
        forTestService.checkServiceRequests(checkP, expServReq, serviceRequestService.active(checkP))

        val registering = serviceRequestService.all(checkP).find { it.code.code() == REGISTERING }!!
        assertEquals(ServiceRequestStatus.completed, registering.status, "wrong status of REGISTERING")

        forTestService.compareListOfString(
                expServReq.mapNotNull { it.locationId }.distinct().map {
                    val location = locationService.byId(it)
                    location.identifierValueNullable(IdentifierType.OFFICE_NUMBER)
                            ?: let { conceptService.byCode(ValueSetName.LOCATION_TYPE, location.type()).display }
                },
                reportService.carePlanToPrint(checkP).locations.map { it.location },
                "wrong location list for care plan to print"
        )
    }
}