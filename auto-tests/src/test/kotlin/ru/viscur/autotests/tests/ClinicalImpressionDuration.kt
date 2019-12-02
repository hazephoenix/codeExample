package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observation1Office101
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.createServiceRequestResource
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class ClinicalImpressionDuration {

    @Test
    fun settingDefaultDuration() {
        //установка значения продолжительности обследования RED пациента
        QueRequests.setDefaultDuration("RED", 700)

        //получение значения продолжительности обследования RED пациента
        val defaultDurationOfRed = QueRequests.getDefaultDuration().find {it.severity == "RED"}?.defaultDuration

        //проверка, что значение установлено
        assertEquals(700, defaultDurationOfRed, "wrong default duration for severity" )
    }

    @Test
    fun gettingPatientClinicalImpressionDuration() {
        //создание пациента
        QueRequests.deleteQue()
        val servRequests = listOf(
                createServiceRequestResource(observation1Office101)
        )
        val bundle1 = bundle("1120", "GREEN", servRequests)

        //получение информации о продолжительности обследования пациента
        val patientId = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))
        val patientDurationInfo = QueRequests.getPatientsClinicalImpressionDuration().find{it.patientId == patientId}!!

        //проверка, что существует информация о продолжительности
        assertNotNull(patientDurationInfo.start)
        assertNotNull(patientDurationInfo.duration)
    }
}