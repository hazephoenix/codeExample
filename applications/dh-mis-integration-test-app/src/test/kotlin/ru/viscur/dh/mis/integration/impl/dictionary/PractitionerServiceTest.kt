package ru.viscur.dh.mis.integration.impl.dictionary

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.autotests.utils.Helpers
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.util.OFFICE_117
import ru.viscur.dh.datastorage.api.util.QUALIFICATION_CATEGORY_SURGEON
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.PractitionerExtension
import ru.viscur.dh.fhir.model.utils.qualificationCategory

/**
 * Created at 23.11.2019 12:28 by SherbakovaMA
 *
 * Тест на методы сервиса [PractitionerService]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Test cleans and modifies db. Practitioners ids are for tests only")
class PractitionerServiceTest {

    @Autowired
    lateinit var practitionerService: PractitionerService

    @Autowired
    lateinit var forTestService: ForTestService

    @Autowired
    lateinit var resourceService: ResourceService

    @Test
    fun testupdateBlockedAndGet() {
        val practitionerId = Helpers.surgeonId
        //предполагается, что изначально нет заблокированных
        val initAll = practitionerService.all()
        val initAllByQualification = practitionerService.byQualificationCategory(QUALIFICATION_CATEGORY_SURGEON)
        assertEquals(practitionerService.all().size, practitionerService.all(withBlocked = true).size)
        checkBlocked(practitionerId, false)

        //блокируем врача
        practitionerService.updateBlocked(practitionerId, true)

        assertEquals(initAllByQualification.size, practitionerService.byQualificationCategory(QUALIFICATION_CATEGORY_SURGEON).size + 1, "количество должно уменьшиться на 1 после блокировки")
        assertEquals(practitionerService.all().size + 1, practitionerService.all(withBlocked = true).size, "незаблокированных должно быть меньше чем всех на 1")
        checkBlocked(practitionerId, true)

        //разблокируем врача
        practitionerService.updateBlocked(practitionerId, false)

        assertEquals(initAllByQualification.size, practitionerService.byQualificationCategory(QUALIFICATION_CATEGORY_SURGEON).size, "количество должно совпадать с начальным")
        assertEquals(initAll.size, practitionerService.all().size, "количество должно совпадать с начальным")
        assertEquals(practitionerService.all().size, practitionerService.all(withBlocked = true).size, "все должны быть незаблокированные")
        checkBlocked(practitionerId, false)
    }

    private fun checkBlocked(practitionerId: String, value: Boolean) {
        assertEquals(value, practitionerService.byId(practitionerId).extension.blocked, "непрпавильное значени блокировки у врача $practitionerId")
    }

    @Test
    fun testWithBlockedFalse() {
        //Количество должно совпадать с переданным значением false и значением по умолчанию
        val practitionerId = Helpers.surgeonId
        assertEquals(practitionerService.all().size, practitionerService.all(withBlocked = false).size)
        practitionerService.updateBlocked(practitionerId, true)
        assertEquals(practitionerService.all().size, practitionerService.all(withBlocked = false).size)
        practitionerService.updateBlocked(practitionerId, false)
        assertEquals(practitionerService.all().size, practitionerService.all(withBlocked = false).size)
    }

    @Test
    fun testCreate() {
        val initSize = practitionerService.all().size
        val createdPractitioner = practitionerService.create(Helpers.createPractitioner())
        val practitionerId = createdPractitioner.id
        try {
            checkBlocked(practitionerId, false)
            assertEquals(initSize + 1, practitionerService.all().size, "количество врачей должно увеличиться после создания")
            assertEquals(QUALIFICATION_CATEGORY_SURGEON, createdPractitioner.qualificationCategory(), "категория должна определиться как 'хирург'")
        } finally {
            resourceService.deleteById(ResourceType.Practitioner, practitionerId)
        }
        assertEquals(initSize, practitionerService.all().size, "количество должно быть как в начале проверки")
    }

    @Test
    fun testUpdate() {
        val initSize = practitionerService.all().size
        val createdPractitioner = practitionerService.create(Helpers.createPractitioner().apply { gender = Gender.male })
        val practitionerId = createdPractitioner.id
        try {
            checkBlocked(practitionerId, false)
            val newValues = Practitioner(
                    id = practitionerId,
                    identifier = createdPractitioner.identifier,
                    name = createdPractitioner.name,
                    gender = Gender.female,
                    qualification = createdPractitioner.qualification,
                    extension = PractitionerExtension(blocked = true, qualificationCategory = createdPractitioner.extension.qualificationCategory)
            )
            val updatedPractitioner = practitionerService.update(newValues)
            val updatedPractitionerId = updatedPractitioner.id
            assertEquals(practitionerId, updatedPractitionerId, "id врача не должен был измениться")
            val actual = practitionerService.byId(updatedPractitionerId)
            checkBlocked(practitionerId, true)
            assertEquals(newValues.gender, actual.gender, "пол должен был обновиться")
        } finally {
            resourceService.deleteById(ResourceType.Practitioner, practitionerId)
        }
        assertEquals(initSize, practitionerService.all().size, "количество должно быть как в начале проверки")
    }

    @Test
    fun `test update on work true for inspection practitioner`() {
        val practitionerId = Helpers.surgeonId
        checkOnWork(practitionerId, false, null)
        practitionerService.updateOnWork(practitionerId, true)
        checkOnWork(practitionerId, true, null)

        //возвращаем значение по умолчанию
        practitionerService.updateOnWork(practitionerId, false)
    }

    @Test
    fun `test update on work true for inspection practitioner, officeId is ignored`() {
        val practitionerId = Helpers.surgeonId
        checkOnWork(practitionerId, false, null)
        practitionerService.updateOnWork(practitionerId, true, "must be ignored")
        checkOnWork(practitionerId, true, null)

        //возвращаем значение по умолчанию
        practitionerService.updateOnWork(practitionerId, false)
    }

    @Test
    fun `test update on work true for diagnostic practitioner`() {
        val practitionerId = Helpers.diagnosticAssistantId
        checkOnWork(practitionerId, false, null)
        practitionerService.updateOnWork(practitionerId, true, OFFICE_117)
        checkOnWork(practitionerId, true, OFFICE_117)

        //возвращаем значение по умолчанию
        practitionerService.updateOnWork(practitionerId, false)
    }

    @Test
    fun `test update on work true for diagnostic practitioner, error if office is not defined`() {
        val practitionerId = Helpers.diagnosticAssistantId
        checkOnWork(practitionerId, false, null)
        Assertions.assertThrows(Exception::class.java) { practitionerService.updateOnWork(practitionerId, true) }
    }

    @Test
    fun `test update on work false for inspection practitioner`() {
        val practitionerId = Helpers.surgeonId
        checkOnWork(practitionerId, false, null)
        practitionerService.updateOnWork(practitionerId, true)
        checkOnWork(practitionerId, true, null)

        practitionerService.updateOnWork(practitionerId, false)
        checkOnWork(practitionerId, false, null)
    }

    private fun checkOnWork(practitionerId: String, expOnWork: Boolean, expOnWorkInOfficeId: String? = null) {
        val practitioner = practitionerService.byId(practitionerId)
        assertEquals(expOnWork, practitioner.extension.onWork, "неправильное значение onWork для $practitionerId")
        assertEquals(expOnWorkInOfficeId, practitioner.extension.onWorkInOfficeId, "неправильное значение onWorkOfficeId для $practitionerId")
    }
}