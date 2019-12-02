package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Constants.Companion.surgeon1Id
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.bundleForDiagnosis
import ru.viscur.autotests.utils.Helpers.Companion.bundleForSeverity
import ru.viscur.autotests.utils.Helpers.Companion.surgeon2Id
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Predictions {

    @Test
    fun predictDiagnosis() {
        val bundle = bundleForDiagnosis("RED")
        //получение и проверка предположительных диагнозов по результатам осмотра фельдшером
        val diagnosisList = QueRequests.getDiagnosis(bundle, "10")
        assertEquals(10, diagnosisList.diagnoses.size, "wrong diagnosis count")
    }

    @Test
    fun predictSeverity() {
        val bundle = bundleForSeverity()
        //получение и проверка степени тяжести пациента по результатам осмотра фельдшером
        val severityResponse = QueRequests.getSeverity(bundle, "2")
        severityResponse.assertThat().body("severity.code", equalTo("GREEN"))
    }

    @Test
    fun predictServRequestsByDiagnosis () {
        //создание диагноза
        val diagnosisCode = "A16"
        val diagnosis = mapOf(
                "diagnosis" to diagnosisCode,
                "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
                "gender" to "male"
        )

        //получение предположительных Service Request по диагнозу
        val servRequestsList = QueRequests.getSupposedServRequests(diagnosis)

        //проверка количества предположительных Service Requests
        assertEquals(18, servRequestsList.size, "wrong number of service requests for diagnosis: $diagnosisCode")
    }

    @Test
    fun predictRespWithLessWorkload(){
        QueRequests.cancelAllActivePatient()
        val diagnosisCode = "Q11"
        val diagnosis = mapOf(
                "diagnosis" to diagnosisCode,
                "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
                "gender" to "male"
        )

        //2 хирурга активны
        QueRequests.setPractitionerActivityAndLocation(surgeon1Id, true)
        QueRequests.setPractitionerActivityAndLocation(surgeon2Id, true)

        //создаем пациента на ответственность surgeon1
        val bundle1 = bundle("1120", "RED")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //проверка, что у surgeon1 есть пациент на ответственности, а у surgeon2 нет ни одного
        val respOfSurgeon1 = QueRequests.getPatientsOfResponsable(surgeon1Id)
        val respOfSurgeon2 = QueRequests.getPatientsOfResponsable(surgeon2Id)
        assertEquals(1, respOfSurgeon1.patients.size, "wrong numbers of patient for $surgeon1Id" )
        assertEquals(0, respOfSurgeon2.patients.size, "wrong numbers of patient for $surgeon2Id" )

        //получаем предположительный список ServiceRequest для следующего пациента
        val predictedResp = QueRequests.getSupposedServRequests(diagnosis).filter {it.code.code() == observationOfSurgeon}.first()

        //проверка, что в полученном списке предположительный ответственный - surgeon2, менее занятый
        assertEquals(surgeon2Id, predictedResp.performer!!.first().id, "wrong performer predicted")
    }
}