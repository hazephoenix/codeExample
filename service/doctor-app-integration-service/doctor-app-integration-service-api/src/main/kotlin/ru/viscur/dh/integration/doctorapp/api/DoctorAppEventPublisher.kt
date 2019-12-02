package ru.viscur.dh.integration.doctorapp.api

import ru.viscur.dh.fhir.model.entity.ClinicalImpression
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.integration.doctorapp.api.model.QueuePatient

interface DoctorAppEventPublisher {

    /**
     * Создан врач (один из доступных для выбора)
     * @param practitioner созданный врач
     */
    fun publishDoctorCreated(practitioner: Practitioner);

    /**
     * Врач удален (один из доступных для выбора)
     * @param ID врача ([Practitioner.id])
     */
    fun publishDoctorRemoved(practitionerId: String);

    /**
     * Изменился статус врача доступного для выбора (стал доступен/недоступен для выбора)
     * @param practitionerId ID врача ([Practitioner.id])
     * @param disabled доступен или нет в данный момент врач для выбора
     */
    fun publishDoctorStatusChanged(practitionerId: String, disabled: Boolean);

    /**
     * Новый пациент в
     * @param targetPractitionersIds список ID врачей который интересует данное изменение в очереди
     * @param clinicalImpression: текущее обследование
     * @param patient новый пациент в очереди
     */
    fun publishNewQueuePatient(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression, patient: QueuePatient)

    /**
     * Пациент ушел из очереди
     * @param targetPractitionersIds список ID врачей который интересует данное изменение в очереди
     * @param clinicalImpression: текущее обследование
     * @param patientId ID пациента
     */
    fun publishQueuePatientRemoved(targetPractitionersIds: Set<String>, patientId: String)

    /**
     * Превышено время обслуживания пациента
     * @param targetPractitionersIds список ID врачей который интересует данное изменение в очереди
     * @param clinicalImpression: текущее обследование
     */
    fun publishPatientServiceTimeElapsed(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression)

    /**
     * Обследование завершено (прошел всех диагностов)
     * @param targetPractitionersIds список ID врачей который интересует данное изменение в очереди
     * @param clinicalImpression: текущее обследование
     */
    fun publishObservationReady(targetPractitionersIds: Set<String>, clinicalImpression: ClinicalImpression)
}