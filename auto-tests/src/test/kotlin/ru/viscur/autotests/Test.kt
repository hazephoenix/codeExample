package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.sql.Timestamp
import java.util.*


@EnableAutoConfiguration
class Test {

    companion object {
        private val log = LoggerFactory.getLogger(Test::class.java)
    }

    @Test
    @Order(1)

    fun redPatientShouldBeFirstInQue() {
        /*val patient = Patient(
                identifier = listOf(
                        //паспорт
                        Identifier(
                                value = "7879 77521487",//серия номер
                                type = CodeableConcept(
                                        systemId = ValueSetName.IDENTIFIER_TYPES.id,
                                        code = IdentifierType.PASSPORT.toString()
                                ),
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = Timestamp(1222223))
                        ),
                        //полис
                        Identifier(
                                value = "7879 77521487",//серия номер
                                type = CodeableConcept(
                                        systemId = ValueSetName.IDENTIFIER_TYPES.id,
                                        code = IdentifierType.DIGITAL_ASSURANCE.toString()
                                ),//|| physicalPolis - полис + вид полиса
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = Timestamp(1222223), end = Timestamp(1222223))//действует с по
                        ),
                        //ЕНП
                        Identifier(value = "7879 77521487",/*серия номер*/ type = IdentifierType.ENP),
                        //СНИЛС
                        Identifier(value = "7879 77521487",/*номер*/ type = IdentifierType.SNILS),
                        //qr браслета
                        Identifier(value = "7879 77521487",/*номер*/ type = IdentifierType.BRACELET)
                ),
                name = listOf(HumanName(text = "Александров И. А.", family = "Александров", given = listOf("Иван", "Алексеевич"))),
                birthDate = Date(),
                gender = Gender.female,
                extension = PatientExtension(
                        nationality = "Russian",//национальность
                        birthPlace = Address(country = "Russia", text = "Россия ТО г. Томск", state = "TO", city = "Tomsk")//место рождения
                )
        )*/




    }
}