package ru.viscur.autotests.tests

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import ru.viscur.autotests.restApi.QueRequests
import ru.viscur.autotests.utils.Constants.Companion.office101Id
import ru.viscur.autotests.utils.Constants.Companion.practitioner1Office101
import ru.viscur.autotests.utils.Helpers.Companion.surgeonId
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.type.HumanName
import ru.viscur.dh.fhir.model.type.PractitionerExtension
import ru.viscur.dh.fhir.model.type.PractitionerQualification
import ru.viscur.dh.fhir.model.utils.now
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

@Disabled("Debug purposes only")
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
    fun getPractitionersWithBlocked() {
        //блокировка practitioner
        QueRequests.blockPractitioner(surgeonId, true)

        //получение списка всех practitioner, включая заблокированных
        val practitionersInfo = QueRequests.getPractitioners(true)

        //проверка, что в списке practitioner есть заблокированный
        assertEquals(true, practitionersInfo.find { it.id == surgeonId }?.extension?.blocked, "wrong $surgeonId status")
    }

    @Test
    fun setPractitionerOnWork () {
        //practitioner не на работе
        QueRequests.setPractitionerActivityAndLocation(practitioner1Office101, false)

        //проверка, что значения установлены
        var practitionerInfo = QueRequests.getPractitionerById(practitioner1Office101)
        assertEquals(false, practitionerInfo.extension.onWork, "wrong OnWork value for $practitioner1Office101")
        assertNull(practitionerInfo.extension.onWorkInOfficeId, "wrong OnWorkInOfficeId value for $practitioner1Office101")

        //practitioner на работе
        QueRequests.setPractitionerActivityAndLocation(practitioner1Office101, true, office101Id)

        //проверка, что значения установлены
        practitionerInfo = QueRequests.getPractitionerById(practitioner1Office101)
        assertEquals(true, practitionerInfo.extension.onWork, "wrong OnWork value for $practitioner1Office101")
        assertEquals(office101Id, practitionerInfo.extension.onWorkInOfficeId, "wrong OnWorkInOfficeId value for $practitioner1Office101")
    }

    @Test
    fun practitionerCreating() {
        val practitionerCountBeforeCreating = QueRequests.getPractitioners().size
        //создание practitioner
        val practitionerName = "Тест " + now()
        val practitioner = Practitioner(
            id = "ignored",
            name = listOf(
                HumanName(
                    text = practitionerName,
                    family = practitionerName,
                    given = listOf("Иван"),
                    suffix = listOf("Алексеевич")
                )
            ),
            qualification = listOf(
                PractitionerQualification(
                    code = CodeableConcept(code = "Ultrasound_doctor", systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id)
                )
            ),
            extension = PractitionerExtension(qualificationCategory = "Test qualification")
        )

        QueRequests.createPractitioner(practitioner)
        val practitionerCountAfterCreating = QueRequests.getPractitioners().size

        //проверка, что practitioner стало на 1 больше после создания
        assertEquals(practitionerCountAfterCreating, practitionerCountBeforeCreating + 1, "wrong number of practitioners" )
    }

    @Test
    fun practitionerUpdating() {
        //создание practitioner
        val practitionerName = "Тест " + now()
        val practitionerUpdatedName = practitionerName + "Updated"
        val practitioner = Practitioner(
            id = "ignored",
            name = listOf(
                HumanName(
                    text = practitionerName,
                    family = practitionerName,
                    given = listOf("Иван"),
                    suffix = listOf("Алексеевич")
                )
            ),
            qualification = listOf(
                PractitionerQualification(
                    code = CodeableConcept(code = "Ultrasound_doctor", systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id)
                )
            ),
            extension = PractitionerExtension(qualificationCategory = "Test qualification")
        )

        val createdPractitioner = QueRequests.createPractitioner(practitioner)

        //обновление информации о practitioner
        val practitionerNewInfo = Practitioner(
            id = createdPractitioner.id,
            name = listOf(
                HumanName(
                    text = practitionerUpdatedName,
                    family = practitionerUpdatedName,
                    given = listOf(practitionerUpdatedName),
                    suffix = listOf(practitionerUpdatedName))),
            qualification = listOf(
                PractitionerQualification(
                    code = CodeableConcept(code = "Ultrasound_doctor", systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id)
                )
            ),
            extension = PractitionerExtension(qualificationCategory = "Test qualification")
        )

        val updatedPractitioner = QueRequests.updatePractitioner(practitionerNewInfo)

        //проверка, что информация о practitioner изменена
        assertEquals(createdPractitioner.id, updatedPractitioner.id, "wrong updated practitioner id")
        assertEquals(practitionerUpdatedName, updatedPractitioner.name.first().text, "wrong updated practitioner name")
        assertEquals(practitionerUpdatedName, updatedPractitioner.name.first().family, "wrong updated practitioner name")
    }

    @Test
    fun getPractitionerById() {
        //получение данных о practitioner
        val surgeonInfo = QueRequests.getPractitionerById(surgeonId)

        //проверка, что ответ корректный
        assertEquals(surgeonId, surgeonInfo.id, "wrong $surgeonId")
    }

    @Test
    fun practitionerBlocking() {

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
        val codeName = "ICD-10"
        val icdListInfo = QueRequests.getCodeInfo(codeName)

        //проверка, что список не пустой
        assertFalse(icdListInfo.isEmpty())
    }

    @Test
    fun getIcdListParentCode() {
        //получение списка кодов диагнозов подветки
        val codeName = "ICD-10"
        val parentCode = "A00"
        val icdListInfo = QueRequests.getCodeInfo(codeName, parentCode)

        //проверка, что список не пустой
        assertFalse(icdListInfo.isEmpty())
    }
}