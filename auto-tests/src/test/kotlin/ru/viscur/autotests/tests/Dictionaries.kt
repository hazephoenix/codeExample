package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests

class Dictionaries {

    @Test
    fun getPractitioners() {
        //получение списка всех practitioner
        val practitionersInfo = QueRequests.getPractitioners()

        //проверка, что список не пустой
        assertFalse(practitionersInfo.isEmpty())
    }

    @Test
    fun getPractitionerById() {
        val surgeonId = "хирург_Иванов"

        //получение данных о practitioner
        val surgeonInfo = QueRequests.getPractitionerById(surgeonId)

        //проверка, что ответ корректный
        assertEquals(surgeonId, surgeonInfo.id, "wrong $surgeonId")
    }

    @Test
    fun practitionerBlocking() {
        val surgeonId = "хирург_Иванов"

        //блокировка practitioner
        QueRequests.blockPractitioner(surgeonId, true)

        //проверка, что practitioner заблокирован
        val surgeonInfo = QueRequests.getPractitionerById(surgeonId)
        assertEquals(true, surgeonInfo.extension.blocked, "wrong $surgeonId status")

        //разблокировка
        QueRequests.blockPractitioner(surgeonId, false)
    }

    @Test
    fun getPractitionersWithBlocked() {
        val surgeonId = "хирург_Иванов"
        //блокировка practitioner
        QueRequests.blockPractitioner(surgeonId, true)

        //получение списка всех practitioner, включая заблокированных
        val practitionersInfo = QueRequests.getPractitioners(true)

        //проверка, что в списке practitioner есть заблокированный
        assertEquals(true, practitionersInfo.find { it.id == surgeonId }?.extension?.blocked, "wrong $surgeonId status")
    }

    @Test
    fun practitionerCreating() {

    }

    @Test
    fun getIcdToObservationTypes() {
        //получение списка всех услуг для диагнозов
        val icdToObsInfo = QueRequests.getIcdToObservationTypes()

        //проверка, что список не пустой
        assertFalse(icdToObsInfo.isEmpty())
    }

    @Test
    fun getObservationTypes() {
        //получение списка всех услуг
        val obsTypesInfo = QueRequests.getObservationTypes()

        //проверка, что список не пустой
        assertFalse(obsTypesInfo.isEmpty())
    }

    @Test
    fun getObservationTypesParentCode() {
        //получение списка услуг подветки
        val parentCode = "X-ray"
        val obsTypesInfo = QueRequests.getObservationTypes(parentCode)

        //проверка, что список не пустой
        assertFalse(obsTypesInfo.isEmpty())
    }

    @Test
    fun getIcdToPractitionersQualification() {
        //получение списка маппинга диагноза к квалификации врача
        val icdToPractQualInfo = QueRequests.getIcdToPractitionerQualification()

        //проверка, что список не пустой
        assertFalse(icdToPractQualInfo.isEmpty())
    }

    @Test
    fun getOffices() {
        //получения списка офисов
        val officesInfo = QueRequests.getOffices()

        //проверка, что список не пустой
        assertFalse(officesInfo.isEmpty())
    }

    @Test
    fun getRespQualificationToObservationTypes() {
        //получение списка маппинга квалификации ответственного врача к обследованиям
        val respQualificationToObservInfo = QueRequests.getRespQualificationToObservationTypes()

        //проверка, что список не пустой
        assertFalse(respQualificationToObservInfo.isEmpty())
    }
}