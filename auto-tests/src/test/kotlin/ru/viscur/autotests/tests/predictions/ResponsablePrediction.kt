package ru.viscur.autotests.tests.predictions

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants
import ru.viscur.autotests.utils.Helpers
import ru.viscur.autotests.utils.patientIdFromServiceRequests
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.resources

@Disabled("Debug purposes only")
class ResponsablePrediction {

    @Test
    fun predictRespWithLessWorkload(){
        QueRequests.cancelAllActivePatient()
        val diagnosisCode = "Q11"
        val diagnosisForSurgeon = mapOf(
            "diagnosis" to diagnosisCode,
            "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
            "gender" to "male"
        )

        //2 хирурга активны
        QueRequests.setPractitionerActivityAndLocation(Constants.SURGEON1_ID, true)
        QueRequests.setPractitionerActivityAndLocation(Constants.SURGEON2_ID, true)

        //создаем пациента на ответственность surgeon1
        val bundle1 = Helpers.bundle("1120", "RED")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //проверка, что у surgeon1 есть пациент на ответственности, а у surgeon2 нет ни одного
        val respOfSurgeon1 = QueRequests.getPatientsOfResponsable(Constants.SURGEON1_ID)
        val respOfSurgeon2 = QueRequests.getPatientsOfResponsable(Constants.SURGEON2_ID)
        Assertions.assertEquals(1, respOfSurgeon1.patients.size, "wrong numbers of patient for ${Constants.SURGEON1_ID}")
        Assertions.assertEquals(0, respOfSurgeon2.patients.size, "wrong numbers of patient for ${Constants.SURGEON2_ID}")

        //получаем предположительный список ServiceRequest для следующего пациента
        val predictedResp = QueRequests.getSupposedServRequests(diagnosisForSurgeon).filter {it.code.code() == Constants.OBSERVATION_OF_SURGEON }.first()

        //проверка, что в полученном списке предположительный ответственный - surgeon2, менее занятый
        Assertions.assertEquals(Constants.SURGEON2_ID, predictedResp.performer!!.first().id, "wrong performer predicted")
    }

    @Test
    fun predictRespWithSameWorkload(){
        QueRequests.cancelAllActivePatient()
        val diagnosisCode = "Q11"
        val diagnosisForSurgeon = mapOf(
            "diagnosis" to diagnosisCode,
            "complaints" to listOf("Сильная боль в правом подреберье", "Тошнит"),
            "gender" to "male"
        )

        //2 хирурга активны
        QueRequests.setPractitionerActivityAndLocation(Constants.SURGEON1_ID, true)
        QueRequests.setPractitionerActivityAndLocation(Constants.SURGEON2_ID, true)

        //создаем пациента на ответственность surgeon1
        val bundle1 = Helpers.bundle("1120", "RED")
        val patientId1 = patientIdFromServiceRequests(QueRequests.createPatient(bundle1).resources(ResourceType.ServiceRequest))

        //создаем пациента на ответственность surgeon2
        val bundle2 = Helpers.bundleForSurgeon2("1121", "RED")
        val patientId2 = patientIdFromServiceRequests(QueRequests.createPatient(bundle2).resources(ResourceType.ServiceRequest))

        //проверка, что у surgeon1 и у surgeon2 есть по одному пациенту на ответственности
        val respOfSurgeon1 = QueRequests.getPatientsOfResponsable(Constants.SURGEON1_ID)
        val respOfSurgeon2 = QueRequests.getPatientsOfResponsable(Constants.SURGEON2_ID)
        Assertions.assertEquals(1, respOfSurgeon1.patients.size, "wrong numbers of patient for ${Constants.SURGEON1_ID}")
        Assertions.assertEquals(1, respOfSurgeon2.patients.size, "wrong numbers of patient for ${Constants.SURGEON2_ID}")

        //получаем предположительный список ServiceRequest для следующего пациента
        val predictedResp = QueRequests.getSupposedServRequests(diagnosisForSurgeon).filter {it.code.code() == Constants.OBSERVATION_OF_SURGEON }.first()

        //проверка, что в полученном списке предположительный ответственный - surgeon1, выбранный по алфавиту
        Assertions.assertEquals(Constants.SURGEON1_ID, predictedResp.performer!!.first().id, "wrong performer predicted")
    }
}