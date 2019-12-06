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
        const val paramedicId = "Paramedic_Сейсенбекова_Алена_Нуржановна"

        /**
         * id мед работника в кабинете диагностики/анализов
         */
        const val diagnosticAssistantId = "Ms_Аникина_Алена_Анатольевна"

        /**
         * id уролога
         */
        const val urologistId = "Urologist_Клюев_Михаил_Васильевич"
        const val urologist2Id = "Urologist_Новиков_Сергей_Игоревич"

        /**
         * id хирурга
         */
        const val surgeonId = "Surgeon_Баширов_Сергей_Рафаэльевич"
        const val surgeon2Id = "Surgeon_Буркин_Максим_Викторович"

        /**
         * Квалификации
         */
        const val QUALIFICATION_SURGEON = "Surgeon"
        const val QUALIFICATION_CATEGORY_SURGEON = "Surgeon_category"

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
        fun bundle(enp: String, severity: String, servRequests: List<ServiceRequest>? = null): Bundle {
            val patient = createPatientResource(enp = enp, severity = enumValueOf(severity))
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity)
            val personalDataConsent = createConsentResource()
            val diagnosticReport = createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
            val diagnostiReportMainSyndrome = createDiagnosticReportResource(diagnosisCode = "A00.1", practitionerId = paramedicId, status = DiagnosticReportStatus.mainSyndrome)
            val list = createPractitionerListResource(surgeonId)
            val claim = createClaimResource()

            val bundle = Bundle(entry = listOf(
                    BundleEntry(patient),
                    BundleEntry(diagnosticReport),
                    BundleEntry(diagnostiReportMainSyndrome),
                    BundleEntry(bodyWeight),
                    BundleEntry(personalDataConsent),
                    BundleEntry(list),
                    BundleEntry(claim),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ) + (servRequests?.map { BundleEntry(it) } ?: emptyList()))
            return bundle
        }

        fun bundleForSurgeon2(enp: String, severity: String, servRequests: List<ServiceRequest>? = null): Bundle {
            val patient = createPatientResource(enp = enp)
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity)
            val personalDataConsent = createConsentResource()
            val diagnosticReport = createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
            val diagnostiReportMainSyndrome = createDiagnosticReportResource(diagnosisCode = "A00.1", practitionerId = paramedicId, status = DiagnosticReportStatus.mainSyndrome)
            val list = createPractitionerListResource(surgeon2Id)
            val claim = createClaimResource()

            val bundle = Bundle(entry = listOf(
                BundleEntry(patient),
                BundleEntry(diagnosticReport),
                BundleEntry(diagnostiReportMainSyndrome),
                BundleEntry(bodyWeight),
                BundleEntry(personalDataConsent),
                BundleEntry(list),
                BundleEntry(claim),
                BundleEntry(questionnaireResponseSeverityCriteria)
            ) + (servRequests?.map { BundleEntry(it) } ?: emptyList()))
            return bundle
        }

        fun bundleForUrologist(enp: String, severity: String, servRequests: List<ServiceRequest>? = null): Bundle {
            val patient = createPatientResource(enp = enp)
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseResource(severity)
            val personalDataConsent = createConsentResource()
            val diagnosticReport = createDiagnosticReportResource(diagnosisCode = "A00.0", practitionerId = paramedicId)
            val diagnostiReportMainSyndrome = createDiagnosticReportResource(diagnosisCode = "A00.1", practitionerId = paramedicId, status = DiagnosticReportStatus.mainSyndrome)
            val list = createPractitionerListResource(urologistId)
            val claim = createClaimResource()

            val bundle = Bundle(entry = listOf(
                    BundleEntry(patient),
                    BundleEntry(diagnosticReport),
                    BundleEntry(diagnostiReportMainSyndrome),
                    BundleEntry(bodyWeight),
                    BundleEntry(personalDataConsent),
                    BundleEntry(list),
                    BundleEntry(claim),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ) + (servRequests?.map { BundleEntry(it) } ?: emptyList()))
            return bundle
        }


        fun bundleForDiagnosis(): Bundle {
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseDiagnosis()
            val bundle = Bundle(entry = listOf(
                    BundleEntry(bodyWeight),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ))
            return bundle
        }

        fun bundleForSeverity(listOfValues: List<String>): Bundle {
            val bodyWeight = createObservation(code = "Weight", valueInt = 90, patientId = "ignored", practitionerId = paramedicId)
            val questionnaireResponseSeverityCriteria = Helpers.createQuestResponseSeverity(listOfValues)
            val bundle = Bundle(entry = listOf(
                    BundleEntry(bodyWeight),
                    BundleEntry(questionnaireResponseSeverityCriteria)
            ))
            return bundle
        }

        //создание ресурсов
        fun createPatientResource(enp: String, queueStatus: PatientQueueStatus = PatientQueueStatus.READY, severity: Severity = Severity.GREEN) = Patient(
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
                                value = "${severity.name.substring(0, 1)}-$enp",//номер
                                type = IdentifierType.QUEUE_CODE
                        )
                ),
                name = listOf(HumanName(text = "Петров Иван Алексеевич", family = "Петров", given = listOf("Иван"), suffix = listOf("Алексеевич"))),
                birthDate = Date(),
                gender = Gender.female,
                extension = PatientExtension(
                        nationality = "Russian",//национальность
                        birthPlace = Address(country = "Russia", text = "Россия ТО г. Томск", state = "TO", city = "Tomsk"),//место рождения
                        queueStatus = queueStatus
                )
        )

        fun createServiceRequestResource(servRequestCode: String, patientId: String = "ignore", status: ServiceRequestStatus = ServiceRequestStatus.active, id: String = genId()) =
                ServiceRequest(
                        id = id,
                        subject = referenceToPatient(patientId),
                        code = CodeableConcept(
                                code = servRequestCode,
                                systemId = ValueSetName.OBSERVATION_TYPES.id
                        ),
                        status = status
                )

        fun createClaimResource(patientId: String = "ignore", id: String = genId()): Claim {
            return Claim(
                    id = id,
                    identifier = listOf(
                            Identifier("ignored", IdentifierType.CLAIM_NUMBER)
                    ),
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
                status: DiagnosticReportStatus = DiagnosticReportStatus.preliminary,
                id: String = genId()
        ) = DiagnosticReport(
                id = id,
                subject = referenceToPatient(patientId),
                performer = listOf(referenceToPractitioner(practitionerId)),
                conclusionCode = listOf(CodeableConcept(code = diagnosisCode, systemId = ValueSetName.ICD_10.id)),
                status = status,
                issued = now()
        )

        fun createConsentResource(patientId: String = "ignored", id: String = genId()) = Consent(
                id = id,
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

        fun createQuestResponseResource(severity: String, patientId: String = "ignore", id: String = genId()) = QuestionnaireResponse(
                id = id,
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
                                linkId = "Patient_can_stand",
                                text = "Опорная функция",
                                answer = listOf(QuestionnaireResponseItemAnswer(
                                        valueCoding = Coding(code = "Can_stand", display = "Может стоять", system = ValueSetName.PATIENT_CAN_STAND.id)
                                ))
                        ),
                        QuestionnaireResponseItem(
                                linkId = "Complaints",
                                text = "Жалобы пациента",
                                answer = listOf(
                                        QuestionnaireResponseItemAnswer(valueString = "лихорадка"),
                                        QuestionnaireResponseItemAnswer(valueString = "Слабость"),
                                        QuestionnaireResponseItemAnswer(valueString = "острая боль")
                                )
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

        fun createQuestResponseDiagnosis(patientId: String = "ignore", id: String = genId()) = QuestionnaireResponse(
            id = id,
            status = QuestionnaireResponseStatus.completed,
            author = referenceToPractitioner("ignored"),
            source = referenceToPatient(patientId),
            questionnaire = "Questionnaire/Severity_criteria",
            item = listOf(
                QuestionnaireResponseItem(
                    linkId = "Complaints",
                    text = "Жалобы пациента",
                    answer = listOf(
                        QuestionnaireResponseItemAnswer(valueString = "Жар"),
                        QuestionnaireResponseItemAnswer(valueString = "Острая боль"),
                        QuestionnaireResponseItemAnswer(valueString = "Головная боль")
                    )
                )
            )
        )

        fun createQuestResponseSeverity(listOfValues: List<String>, patientId: String = "ignore", id: String = genId()) = QuestionnaireResponse(
            id = id,
            status = QuestionnaireResponseStatus.completed,
            author = referenceToPractitioner("ignored"),
            source = referenceToPatient(patientId),
            questionnaire = "Questionnaire/Severity_criteria",
            item = listOf(
                QuestionnaireResponseItem(
                    linkId = "Upper_respiratory_airway",
                    text = "Результат осмотра верхних дыхательных путей",
                    answer = listOf(QuestionnaireResponseItemAnswer(
                        valueCoding = Coding(code = listOfValues.first(), display = listOfValues.first(), system = ValueSetName.UPPER_RESPIRATORY_AIRWAY.id)
                    ))
                ),
                QuestionnaireResponseItem(
                    linkId = "Consciousness_assessment",
                    text = "Сознание",
                    answer = listOf(QuestionnaireResponseItemAnswer(
                        valueCoding = Coding(code = listOfValues.get(1), display = listOfValues.get(1), system = ValueSetName.CONSCIOUSNESS_ASSESSMENT.id)
                    ))
                ),
                QuestionnaireResponseItem(
                    linkId = "Patient_can_stand",
                    text = "Опорная функция",
                    answer = listOf(QuestionnaireResponseItemAnswer(
                        valueCoding = Coding(code = listOfValues.get(2), display = listOfValues.get(2), system = ValueSetName.PATIENT_CAN_STAND.id)
                    ))
                ),
                QuestionnaireResponseItem(
                    linkId = "Complaints",
                    text = "Жалобы пациента",
                    answer = listOf(
                        QuestionnaireResponseItemAnswer(valueString = "Жар"),
                        QuestionnaireResponseItemAnswer(valueString = "Острая боль"),
                        QuestionnaireResponseItemAnswer(valueString = "Головная боль")
                    )
                )

            )
        )


        fun createQuestResponseResourceWithCommonInfo(patientId: String = "ignore", id: String = genId()) = QuestionnaireResponse(
                id = id,
                status = QuestionnaireResponseStatus.completed,
                author = referenceToPractitioner("ignored"),
                source = referenceToPatient(patientId),
                questionnaire = "Questionnaire/Common_info",
                item = listOf(
                        QuestionnaireResponseItem(
                                linkId = "Entry_type",
                                answer = listOf(QuestionnaireResponseItemAnswer(
                                        valueCoding = Coding(code = "Personal_encounter", system = ValueSetName.ENTRY_TYPES.id)
                                ))
                        ),
                        QuestionnaireResponseItem(
                                linkId = "Transportation_type",
                                answer = listOf(QuestionnaireResponseItemAnswer(
                                        valueCoding = Coding(code = "Sitting", system = ValueSetName.TRANSPORTATION_TYPES.id)
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
                id = id,
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
                status = status
        )

        fun createPractitionerListResource(practitionerId: String, id: String = genId()) = ListResource(
                id = id,
                entry = listOf(ListResourceEntry(item = referenceToPractitioner(practitionerId)))
        )

        fun createEncounter(hospitalizationStr: String, patientId: String = "ignored", id: String = genId()) = Encounter(
                id = id,
                subject = referenceToPatient(patientId),
                hospitalization = EncounterHospitalization(destination = Reference(display = hospitalizationStr))
        )

        fun createListResource(patientId: String, officeId: String) = ListResource(entry = listOf(
                ListResourceEntry(item = referenceToPatient(patientId)),
                ListResourceEntry(item = referenceToLocation(officeId))
        ))

        fun createCarePlan(patientId: String, serviceRequests: List<ServiceRequest>, id: String = genId()) = CarePlan(
                id = id,
                author = referenceToPractitioner(paramedicId),
                contributor = referenceToPractitioner(surgeonId),
                created = now(),
                subject = referenceToPatient(patientId),
                activity = serviceRequests.map { CarePlanActivity(outcomeReference = Reference(it)) }
        )

        fun createClinicalImpression(patientId: String, severity: Severity, supportingInfo: List<Reference>, id: String = genId()) = ClinicalImpression(
                id = id,
                status = ClinicalImpressionStatus.active,
                date = now(),
                subject = referenceToPatient(patientId),
                assessor = referenceToPractitioner(surgeonId), // ответственный врач
                summary = "Заключение: направлен на обследования по маршрутному листу",
                supportingInfo = supportingInfo,
                extension = ClinicalImpressionExtension(
                        severity = severity,
                        queueCode = severity.display.substring(0, 1) + "00" + counter++
                )
        )

        fun createPractitioner() = Practitioner(
                id = "ignored",
                name = listOf(HumanName(text = "Петров Иван Алексеевич", family = "Петров", given = listOf("Иван"), suffix = listOf("Алексеевич"))),
                qualification = listOf(PractitionerQualification(code = CodeableConcept(code = QUALIFICATION_SURGEON, systemId = ValueSetName.PRACTITIONER_QUALIFICATIONS.id))),
                extension = PractitionerExtension(qualificationCategory = QUALIFICATION_CATEGORY_SURGEON)
        )
    }
}