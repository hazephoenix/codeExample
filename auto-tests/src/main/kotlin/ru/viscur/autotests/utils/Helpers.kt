package ru.viscur.autotests.utils

import io.restassured.RestAssured
import io.restassured.http.ContentType
import io.restassured.specification.RequestSpecification
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.IdentifierType
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import java.util.*

class Helpers {


    companion object {

        private var counter = 0

        /**
         * id фельдшера
         */
        const val paramedicId = "фельдшер_Колосова"

        /**
         * id мед работника в кабинете диагностики/анализов
         */
        const val diagnosticAssistantId = "мед_работник_диагностики_Сидорова"

        /**
         * id хирурга
         */
        const val surgeonId = "хирург_Петров"
        const val surgeon2Id = "хирург_Иванов"

        //создать спецификацию запроса RestApi
        fun createRequestSpec(body: Any): RequestSpecification {
            return RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745").body(body)
        }

        fun createRequestSpecWithoutBody(): RequestSpecification =
                RestAssured.given().header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745")

        fun createRequestWithQuery(paramsMap: Map <String, Any>): RequestSpecification =
                RestAssured.given().queryParams(paramsMap).header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745")

        fun createRequestWithQueryAndBody(body: Any, paramsMap: Map <String, Any>): RequestSpecification =
                RestAssured.given().queryParams(paramsMap).body(body).header("Content-type", ContentType.JSON).auth().preemptive().basic("test", "testGGhdJpldczxcnasw8745")

        //создание bundle для пациента
        fun bundle(enp: String, severity: String, servRequests: List<ServiceRequest>): Bundle {
            val patient = createPatientResource(enp = enp)
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity)
            val personalDataConsent = createConsentResource()
            val diagnosticReport = createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
            val list = createPractitionerListResource(surgeonId)
            val claim = createClaimResource()

            val bundle = Bundle(entry = listOf(
                    BundleEntry(patient),
                    BundleEntry(diagnosticReport),
                    BundleEntry(bodyWeight),
                    BundleEntry(personalDataConsent),
                    BundleEntry(list),
                    BundleEntry(claim),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ) + servRequests.map { BundleEntry(it) })
            return bundle
        }

        fun bundleForDiagnosis(severity: String): Bundle {
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity)
            val bundle = Bundle(entry = listOf(
                    BundleEntry(bodyWeight),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ))
                    return bundle
        }

        fun bundleForSeverity(): Bundle {
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource("ignored")
            val bundle = Bundle(entry = listOf(
                    BundleEntry(bodyWeight),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ))
            return bundle
        }

        //создание ресурсов
        fun createPatientResource(enp: String, queueStatus: PatientQueueStatus = PatientQueueStatus.READY) = Patient(
                identifier = listOf(
                        Identifier(
                                value = "7878 77521487",//серия номер
                                type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.PASSPORT.toString()),
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = now()),//дата выдачи
                                use = IdentifierUse.official//статус паспорта
                        ),
                        //полис
                        Identifier(
                                value = "7878 77521487",//серия номер
                                type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.DIGITAL_ASSURANCE.toString()),//|| physicalPolis - полис + вид полиса
                                assigner = Reference(display = "ОУФМС по ТО..."),//кем выдан
                                period = Period(start = now(), end = now()),//действует с по
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
                                value = "З-018",//номер
                                type = IdentifierType.QUEUE_CODE
                        )
                ),
                name = listOf(HumanName(text = "Петров И. А.", family = "Петров", given = listOf("Иван", "Алексеевич"))),
                birthDate = Date(),
                gender = Gender.female,
                extension = PatientExtension(
                        nationality = "Russian",//национальность
                        birthPlace = Address(country = "Russia", text = "Россия ТО г. Томск", state = "TO", city = "Tomsk"),//место рождения
                        queueStatus = queueStatus
                )
        )

        fun createServiceRequestResource(servRequestCode: String, patientId: String = "ignore", status: ServiceRequestStatus = ServiceRequestStatus.active) =
                ServiceRequest(
                        subject = referenceToPatient(patientId),
                        code = CodeableConcept(
                                code = servRequestCode,
                                systemId = ValueSetName.OBSERVATION_TYPES.id
                        ),
                        status = status
                )

        fun createClaimResource(patientId: String = "ignore"): Claim {
            return Claim(
                    identifier = listOf(Identifier(value = "123/012345", type = CodeableConcept(systemId = ValueSetName.IDENTIFIER_TYPES.id, code = IdentifierType.CLAIM_NUMBER.toString()))),//номер обращения
                    patient = referenceToPatient(patientId),
                    accident = ClaimAccident(
                            date = Date(), //дата и время происшествия
                            type = CodeableConcept(code = "Вывих", systemId = "ValueSet/tt"),//тип травмы //todo нет списка типов травм
                            locationAddress = Address(text = "В районе пр. Ленина", country = "Russia", state = "TO", city = "Tomsk")//место происшествия
                    )
            )
        }

        fun createDiagnosticReportResource(
                diagnosisCode: String,
                patientId: String = "ignored",
                practitionerId: String = paramedicId,
                status: DiagnosticReportStatus = DiagnosticReportStatus.preliminary
        ) = DiagnosticReport(
                subject = referenceToPatient(patientId),
                performer = listOf(referenceToPractitioner(practitionerId)),
                conclusionCode = listOf(CodeableConcept(code = diagnosisCode, systemId = ValueSetName.ICD_10.id)),
                status = status,
                issued = now()
        )

        fun createConsentResource(patientId: String = "ignore") = Consent(
                category = listOf(CodeableConcept(
                        code = "PERSONAL_DATA",
                        systemId = ValueSetName.CONSENT_CATEGORIES.id,
                        display = "Согласие на обарботку ПДн"
                )),
                dateTime = Date(now().time - 120 * MILLISECONDS_IN_SECOND),
                patient = referenceToPatient(patientId),
                performer = referenceToPractitioner("ignored"),
                organization = listOf(Reference(Organization(name = "СибГМУ")))
        )

        fun createQuestResponseResource(severity: String, patientId: String = "ignore") = QuestionnaireResponse(
                status = QuestionnaireResponseStatus.completed,
                author = referenceToPractitioner("ignored"),
                source = referenceToPatient(patientId),
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
                                        valueCoding = Coding(code = severity, display = "Красный", system = ValueSetName.SEVERITY.id)
                                ))
                        )
                )
        )

        fun createObservation(code: String = "ignored",
                              practitionerId: String = "ignored",
                              patientId: String = "ignored",
                              valueInt: Int? = null,
                              valueString: String? = null,
                              basedOnServiceRequestId: String? = null,
                              status: ObservationStatus = ObservationStatus.registered,
                              id: String = genId()
        ) = Observation(
                performer = listOf(referenceToPractitioner(practitionerId)),
                subject = referenceToPatient(patientId),
                issued = now(),
                code = CodeableConcept(
                        code = code,
                        systemId = ValueSetName.OBSERVATION_TYPES.id,
                        display = "Измерение $code"
                ),
                valueInteger = valueInt,
                valueString = valueString,
                basedOn = basedOnServiceRequestId?.let { Reference(resourceType = ResourceType.ServiceRequest.id, id = basedOnServiceRequestId) },
                status = status,
                id = id
        )

        fun createPractitionerListResource(practitionerId: String) = ListResource(
                entry = listOf(ListResourceEntry(item = referenceToPractitioner(practitionerId)))
        )

        fun createEncounter(hospitalizationStr: String, patientId: String = "ignored") = Encounter(
                subject = referenceToPatient(patientId),
                hospitalization = EncounterHospitalization(destination = Reference(display = hospitalizationStr))
        )

        fun createListResource(patientId: String, officeId: String) = ListResource(entry = listOf(
                ListResourceEntry(item = referenceToPatient(patientId)),
                ListResourceEntry(item = referenceToLocation(officeId))
        ))

        fun createCarePlan(patientId: String, serviceRequests: List<ServiceRequest>) = CarePlan(
                author = referenceToPractitioner(paramedicId),
                contributor = referenceToPractitioner(surgeonId),
                created = now(),
                subject = referenceToPatient(patientId),
                activity = serviceRequests.map { CarePlanActivity(outcomeReference = Reference(it)) }
        )

        fun createClinicalImpression(patientId: String, severity: Severity, supportingInfo: List<Reference>) = ClinicalImpression(
                status = ClinicalImpressionStatus.active,
                date = now(),
                subject = referenceToPatient(patientId),
                assessor = referenceToPractitioner(surgeonId), // ответственный врач
                summary = "Заключение: направлен на обследования по маршрутному листу",
                supportingInfo = supportingInfo,
                extension = ClinicalImpressionExtension(
                        severity = severity,
                        queueNumber = severity.display.substring(0, 1) + "00" + counter++
                )
        )
    }
}