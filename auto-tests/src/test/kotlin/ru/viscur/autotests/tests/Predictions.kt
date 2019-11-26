package ru.viscur.autotests.tests

import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.tests.QueueSorting.Companion.observation1Office101
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.Reference
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class Predictions {

   /* @Test
    fun predictDiagnosis() {
        val expectedDiagnosis = "A00"
        val bundle = Helpers.bundleForDiagnosis("RED")
        //получени и проверка предположительных диагнозов по результатам осмотра фельдшером
        val diagnosisList = QueRequests.getDiagnosis(bundle, "2")
        assertEquals(2, diagnosisList.diagnoses.size, "wrong diagnosis count")
        assertEquals(expectedDiagnosis, diagnosisList.diagnoses.first().code, "wrong diagnosis")
    }*/

    @Test
    fun predictSeverity() {
        val bundle = Helpers.bundleForSeverity()
        //получение и проверка степени тяжести пациента по результатам осмотра фельдшером
        val severityResponse = QueRequests.getSeverity(bundle)
        severityResponse.
                assertThat().body("severity.code", equalTo("GREEN"))
    }

    @Test
    fun predictRequestsByDiagnosis () {
        val diagnosis = " {\"diagnosis\": \"A01\"," +
                "\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"]," +
                "\"gender\": \"male\"}"

        //получение предположительных Service Request по диагнозу и проверка
        val predictedServReq = QueRequests.getSupposedServRequests(diagnosis)
        assertEquals (5, predictedServReq.size, "wrong service requests number")
    }

    @Test
    fun predictRespWithLessWorkload(){
        //отмена всех активных ClinicalImpression
        QueRequests.cancelAllActivePatient()
        val diagnosis = " {\"diagnosis\": \"A01\"," +
                "\"complaints\": [\"Сильная боль в правом подреберье\", \"Тошнит\"]," +
                "\"gender\": \"male\"}"
        val surgeon1 = Helpers.surgeonId
        val surgeon2 = Helpers.surgeon2Id

        //создаем пациента на ответственность surgeon1
        val servRequests = listOf(
                Helpers.createServiceRequestResource(observation1Office101)
        )
        val bundle1 = Helpers.bundle("1120", "GREEN", servRequests)
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //получаем предположительный список ServiceRequest для следующего пациента
        val predictedServRequests = QueRequests.getSupposedServRequests(diagnosis)
        val predictedResp = predictedServRequests.find { it.code.code() == "СтХир" }?.performer?.first()?.id

        //проверка, что в полученном списке предположительный ответственный - surgeon2, менее занятый
        assertEquals(surgeon2, predictedResp, "wrong performer predicted")
    }
}