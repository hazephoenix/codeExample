package ru.viscur.dh.integration.doctorapp.api

import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.integration.doctorapp.api.model.QueuePatientAppDto

interface DoctorAppEventPublisher {

    /**
     * Создан врач (один из доступных для выбора)
     * @param practitioner созданный врач
     */
    fun publishPractitionerCreated(practitioner: Practitioner);

    /**
     * Врач удален (один из доступных для выбора)
     * @param practitionerId ID врача ([Practitioner.id])
     */
    fun publishPractitionerRemoved(practitionerId: String);

    /**
     * Изменился статус врача доступного для выбора (стал доступен/недоступен для выбора)
     * @param practitionerId ID врача ([Practitioner.id])
     * @param disabled доступен или нет в данный момент врач для выбора
     */
    fun publishPractitionerStatusChanged(practitionerId: String, disabled: Boolean);

    /**
     * Новый пациент в очереди для врачей
     * @param targetPractitionersIds список ID врачей который интересует данное изменение в очереди
     * @param clinicalImpression текущее обращение
     * @param patient новый пациент в очереди
     */
    fun publishNewQueuePatient(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression, patient: QueuePatientAppDto)

    /**
     * Пациент ушел из очереди
     * @param targetPractitionersIds список ID врачей которых интересует данное изменение в очереди
     * @param patientId ID пациента
     */
    fun publishQueuePatientRemoved(targetPractitionersIds: Set<String>, patientId: String)

    /**
     * Превышено регламентное время обслуживания обращения пациента
     * @param targetPractitionersIds список ID врачей которых интересует данное изменение в очереди
     * @param clinicalImpression текущее обращение
     */
    fun publishPatientServiceTimeElapsed(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression)

    /**
     * Результаты всех обследований готовы, можно проводить осмотр ответственного
     * @param targetPractitionersIds список ID врачей которых интересует данное изменение в очереди
     * @param clinicalImpression текущее обращение
     */
    fun publishObservationsResultsAreReady(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression)
}