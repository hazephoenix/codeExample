package ru.viscur.autotests.utils

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.referenceToPractitioner
import ru.viscur.dh.fhir.model.valueSets.BundleType
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.nio.file.Files
import java.nio.file.Paths
import java.sql.Timestamp
import java.util.*

class Helpers {


    companion object {

        //создать спецификацию запроса RestApi
        fun createRequestSpec(body: Any): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").body(body)
        }

        fun createRequestSpecWithoutBody(): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745")
        }

        //создание ресурсов
        fun createPatientResource(enp: String): Patient {
            return Patient(
                    identifier = listOf(
                            Identifier(
                                    value = "7878 77521487",//серия номер
                                    type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.PASSPORT.toString()),
                                    assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                    period = Period(start = Timestamp(1222222)),//дата выдачи
                                    use = IdentifierUse.official//статус паспорта
                            ),
                            //полис
                            Identifier(
                                    value = "7878 77521487",//серия номер
                                    type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.DIGITAL_ASSURANCE.toString()),//|| physicalPolis - полис + вид полиса
                                    assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                    period = Period(start = Timestamp(1222222), end = Timestamp(1222222)),//действует с по
                                    use = IdentifierUse.official//статус полиса ||old
                            ),
                            Identifier(
                                    value = "$enp 77521487",//серия номер
                                    type = IdentifierType.ENP
                            ),
                            Identifier(
                                    value = "7878 77521487",//номер
                                    type = IdentifierType.SNILS
                            ),
                            Identifier(
                                    value = "7878 77521487",//номер
                                    type = IdentifierType.BRACELET
                            )
                    ),
                    name = listOf(HumanName(text = "Петров И. А.", family = "Петров", given = listOf("Иван", "Алексеевич"))),
                    birthDate = Date(),
                    gender = Gender.female,
                    extension = PatientExtension(
                            nationality = "Russian",//национальность
                            birthPlace = Address(country = "Russia", text = "Россия ТО г. Томск", state = "TO", city = "Tomsk")//место рождения
                    )
            )
        }

        fun createServiceRequestResource(servRequestCode: String, patientReference: Reference): ServiceRequest {
            return ServiceRequest(
                    subject = patientReference,
                    code = CodeableConcept(
                            code = servRequestCode,
                            systemId = ValueSetName.OBSERVATION_TYPES.id,
                            display = "$servRequestCode"
                    )
            )
        }

        fun createClaimResource(patientReference: Reference): Claim {
           return Claim(
                    identifier = listOf(Identifier(value = "123/012345", type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.CLAIM_NUMBER.toString()))),//номер обращения
                    patient = patientReference,
                    accident = ClaimAccident(
                            date = Date(), //дата и время происшествия
                            type = CodeableConcept(code = "Вывих", systemId = "ValueSet/tt"),//тип травмы //todo нет списка типов травм
                            locationAddress = Address(text = "В районе пр. Ленина", country = "Russia", state = "TO", city = "Tomsk")//место происшествия
                    )
            )
        }

        fun createDiagnosticReportResource(diagnosisCode: String, patientReference: Reference): DiagnosticReport {
            return DiagnosticReport(
                    subject = patientReference,
                    performer = listOf(referenceToPractitioner("ignored")),
                    conclusionCode = listOf(CodeableConcept(code = "A00.0", systemId = ValueSetName.ICD_10.id)),
                    status = DiagnosticReportStatus.preliminary,
                    issued = Timestamp(546568754)
            )
        }

        fun createConsentResource(patientReference: Reference): Consent {
            return Consent(
                    category = listOf(CodeableConcept(
                            code = "PERSONAL_DATA",
                            systemId = ValueSetName.CONSENT_CATEGORIES.id,
                            display = "Согласие на обарботку ПДн"
                    )),
                    dateTime = Timestamp(3424242),
                    patient = patientReference,
                    performer = referenceToPractitioner("ignored"),
                    organization = listOf(Reference(Organization(name = "СибГМУ")))
            )
        }

        fun createQuestResponseResource(patientReference: Reference): QuestionnaireResponse {
            return QuestionnaireResponse(
                    status = QuestionnaireResponseStatus.completed,
                    author = referenceToPractitioner("ignored"),
                    source = patientReference,
                    questionnaire = "Questionnaire/Severity_criteria",
                    item = listOf(
                            QuestionnaireResponseItem(
                                    linkId = "Upper_respiratory_airway",
                                    text = "Результат осмотра верхних дыхательных путей",
                                    answer = listOf(QuestionnaireResponseItemAnswer(
                                            valueCoding = Coding(code = "Airways_passable", display = "Дыхательные пути проходимы", system = ValueSetName.UPPER_RESPIRATORY_AIRWAY.id)
                                    ))
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Consciousness_assessment",
                                    text = "Сознание",
                                    answer = listOf(QuestionnaireResponseItemAnswer(
                                            valueCoding = Coding(code = "Clear_mind", display = "Ясное сознание", system = ValueSetName.CONSCIOUSNESS_ASSESSMENT.id)
                                    ))
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Pain_intensity_assessment",
                                    text = "Оценка интенсивности боли (0-10)",
                                    answer = listOf(QuestionnaireResponseItemAnswer(
                                            valueCoding = Coding(code = "From_0_to_3", display = "0-3", system = ValueSetName.PAIN_INTENSITY_ASSESSMENT.id)
                                    ))
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Patient_can_stand",
                                    text = "Опорная функция",
                                    answer = listOf(QuestionnaireResponseItemAnswer(
                                            valueCoding = Coding(code = "Can_stand", display = "Может стоять", system = ValueSetName.PATIENT_CAN_STAND.id)
                                    ))
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Questionnaire/paramedic-qa-form/complaints",
                                    text = "Жалобы пациента",
                                    answer = listOf(
                                            QuestionnaireResponseItemAnswer(valueString = "Озноб"),
                                            QuestionnaireResponseItemAnswer(valueString = "Слабость"),
                                            QuestionnaireResponseItemAnswer(valueString = "Недомогание")
                                    )
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Questionnaire/paramedic-qa-form/base-syndrom",
                                    text = "Ведущий синдром",
                                    answer = listOf(QuestionnaireResponseItemAnswer(valueString = "Высокая температура"))
                            ),
                            QuestionnaireResponseItem(
                                    linkId = "Severity",
                                    text = "Категория пациента",
                                    answer = listOf(QuestionnaireResponseItemAnswer(
                                            valueCoding = Coding(code = "RED", display = "Красный", system = ValueSetName.SEVERITY.id)
                                    ))
                            )
                    )
            )
        }

        fun createObservation(code: String, valueInt: Int, patientReference: Reference): Observation {
            return Observation(
                    performer = listOf(referenceToPractitioner("ignored")),
                    subject = patientReference,
                    issued = Timestamp(3424242),
                    code = CodeableConcept(
                            code = code,
                            systemId = ValueSetName.OBSERVATION_TYPES.id,
                            display = "Измерение $code"
                    ),
                    valueInteger = valueInt
            )
        }

        fun createPractitionerListResource(practitionerId: String): ListResource {
            return ListResource(
                    entry = listOf(ListResourceEntry(item = referenceToPractitioner(practitionerId)))
            )
        }

        /*fun createPatientAndLocationListResource (patientReference: Reference, locationReference: Reference): ListResource {
            return ListResource(
                    entry = listOf(
                            ListResourceEntry(
                                    item = patientReference
                            ),
                            ListResourceEntry(
                                    item = locationReference)
                            )
                    )
        }*/
    }

}