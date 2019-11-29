package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.Constants.Companion.observationOfSurgeon
import ru.viscur.autotests.utils.Helpers.Companion.bundle
import ru.viscur.autotests.utils.Helpers.Companion.bundleForDiagnosis
import ru.viscur.autotests.utils.Helpers.Companion.bundleForSeverity
import ru.viscur.autotests.utils.Helpers.Companion.surgeon2Id
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

//@Disabled("Debug purposes only")
class Predictions {

    @Test
    fun predictDiagnosis() {
        val bundle = bundleForDiagnosis("RED")
        //получение и проверка предположительных диагнозов по результатам осмотра фельдшером
        val diagnosisList = QueRequests.getDiagnosis(bundle, "100")
        assertEquals(100, diagnosisList.diagnoses.size, "wrong diagnosis count")
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
    //todo отремонтировать
    fun predictRespWithLessWorkload(){
        //отмена всех активных ClinicalImpression
        QueRequests.cancelAllActivePatient()
        val diagnosisCode = "Q11"
        val diagnosis = mapOf(
                "diagnosis" to diagnosisCode,
                "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
                "gender" to "male"
        )

        //создаем пациента на ответственность surgeon1
        val bundle1 = bundle("1120", "RED")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //получаем предположительный список ServiceRequest для следующего пациента
        val predictedResp = QueRequests.getSupposedResp(diagnosis)

        //проверка, что в полученном списке предположительный ответственный - surgeon2, менее занятый
        //assertEquals(surgeon2Id, predictedResp, "wrong performer predicted")
    }
}