package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.springframework.beans.factory.annotation.*
import org.springframework.boot.autoconfigure.*
import org.springframework.boot.test.context.*
import ru.viscur.dh.datastorage.api.*
import ru.viscur.dh.datastorage.impl.config.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.*
import java.sql.*
import java.util.Date

@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only")
class ObservationServiceImplTest {
    @Autowired
    lateinit var resourceService: ResourceService
    @Autowired
    lateinit var observationService: ObservationService

    @Test
    @Order(1)
    fun `should return ServiceRequest by observation`() {
        val createdResources = mutableListOf<BaseResource>()
        val patient = Patient(
                identifier = listOf(
                        //паспорт
                        Identifier(
                                value = "7878 77521487",//серия номер
                                type = CodeableConcept(
                                        systemId = ValueSetName.IDENTIFIER_TYPES.id,
                                        code = IdentifierType.PASSPORT.toString()
                                ),
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = Timestamp(1222222))
                        ),
                        //полис
                        Identifier(
                                value = "7878 77521487",//серия номер
                                type = CodeableConcept(
                                        systemId = ValueSetName.IDENTIFIER_TYPES.id,
                                        code = IdentifierType.DIGITAL_ASSURANCE.toString()
                                ),//|| physicalPolis - полис + вид полиса
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = Timestamp(1222222), end = Timestamp(1222222))//действует с по
                        ),
                        //ЕНП
                        Identifier(value = "7878 77521487",/*серия номер*/ type = IdentifierType.ENP),
                        //СНИЛС
                        Identifier(value = "7878 77521487",/*номер*/ type = IdentifierType.SNILS),
                        //qr браслета
                        Identifier(value = "7878 77521487",/*номер*/ type = IdentifierType.BRACELET)
                ),
                name = listOf(HumanName(text = "Петров И. А.", family = "Петров", given = listOf("Иван", "Алексеевич"))),
                birthDate = Date(),
                gender = Gender.female,
                extension = PatientExtension(
                        nationality = "Russian",//национальность
                        birthPlace = Address(country = "Russia", text = "Россия ТО г. Томск", state = "TO", city = "Tomsk")//место рождения
                )
        ).let { resourceService.create(it)!! }.also { createdResources.add(it) }
        val practitioner = Practitioner(
                identifier = listOf(
                        //rfid
                        Identifier(
                                value = "wrewfwefwe",
                                type = IdentifierType.RFID
                        )
                ),
                name = listOf(HumanName(text = "Петров И. А.", family = "Петров", given = listOf("Иван", "Алексеевич"))),
                qualification = PractitionerQualification(
                        code = CodeableConcept(systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id, code = "Hirurg"),
                        period = Period(Timestamp(1), Timestamp(2))
                )

        ).let { resourceService.create(it)!! }.also { createdResources.add(it) }
        val servReq = ServiceRequest(
                subject = Reference(patient),
                code = CodeableConcept(
                        code = "HIRURG",
                        systemId = ValueSetName.OBSERVATION_TYPES.id,
                        display = "Осмотр хирурга"
                ),
                extension = ServiceRequestExtension(executionOrder = 1)
        ).let { resourceService.create(it)!! }.also { createdResources.add(it) }
        val observation = Observation(
                basedOn = Reference(servReq),
                subject = Reference(patient),
                performer = listOf(Reference(practitioner)),//кто по факту сделал
                code = CodeableConcept(
                        code = "HIRURG",
                        systemId = "",
                        display = "Осмотр хирурга"
                ),
                valueString = "Слизистые носоглотки без изменений",
                issued = Timestamp(1212)
        ).let { resourceService.create(it)!! }.also { createdResources.add(it) }

        observation.status = ObservationStatus.final
        val updated = observationService.update(observation)
        assertEquals(updated?.resourceType, ResourceType.Observation.id)

        // TODO: finalize deleting test resources
        createdResources.forEach {
            resourceService.deleteById(ResourceType.byId(it.resourceType), it.id!!)
        }
    }
}