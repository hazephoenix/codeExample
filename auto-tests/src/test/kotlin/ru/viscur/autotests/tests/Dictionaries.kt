package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.PractitionerQualification
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

//@Disabled("Debug purposes only")
class Dictionaries {

    @Test
    fun getPractitioners() {
        //получение списка всех practitioner
        val practitionersInfo = QueRequests.getPractitioners()

        //проверка, что список не пустой
        assertFalse(practitionersInfo.isEmpty())

        //проверка, что в списке нет заблокированных
        assertNull(practitionersInfo.find { it.extension.blocked == true }?.id)
    }

    @Test
    fun practitionerCreating() {
        val practitionerCountBeforeCreating = QueRequests.getPractitioners().size
        //создание practitioner
        val practitionerName = "Тест " + now()
        val practitioner = Practitioner(
                id = "ignored",
                name = listOf(HumanName(text = practitionerName, family = practitionerName, given = listOf("Иван"), suffix = listOf("Алексеевич"))),
                qualification = PractitionerQualification(code = CodeableConcept(code = "Test", systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id))
        )

        QueRequests.createPractitioner(practitioner)
        val practitionerCountAfterCreating = QueRequests.getPractitioners().size

        //проверка, что practitioner стало на 1 больше после создания
        assertEquals(practitionerCountAfterCreating, practitionerCountBeforeCreating + 1, "wrong number of practitioners" )
    }

    @Test
    fun practitionerUpdating() {
       
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
        var surgeonInfo = QueRequests.getPractitionerById(surgeonId)
        assertEquals(true, surgeonInfo.extension.blocked, "wrong $surgeonId status")

        //разблокировка
        QueRequests.blockPractitioner(surgeonId, false)

        //проверка, что practitioner разблокирован
        surgeonInfo = QueRequests.getPractitionerById(surgeonId)
        assertEquals(false, surgeonInfo.extension.blocked, "wrong $surgeonId status")
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

    @Test
    fun getIcdList() {
        //получение списка кодов диагнозов
        val icdListInfo = QueRequests.getCodeInfo("ICD-10")

        //проверка, что список не пустой
        assertFalse(icdListInfo.isEmpty())
    }

    @Test
    fun getIcdListParentCode() {
        //получение списка кодов диагнозов
        val icdListInfo = QueRequests.getCodeInfo("ICD-10", "A00")

        //проверка, что список не пустой
        assertFalse(icdListInfo.isEmpty())
    }
}