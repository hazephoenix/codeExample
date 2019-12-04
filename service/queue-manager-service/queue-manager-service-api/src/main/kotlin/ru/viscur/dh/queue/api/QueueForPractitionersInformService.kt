package ru.viscur.dh.queue.api

import ru.viscur.dh.fhir.model.entity.ClinicalImpression

/**
 * Created at 19.11.2019 11:33 by SherbakovaMA
 *
 * Сервис информирования мед. работников об изменениях очереди к ним (добавлен/удален пациент)
 */
interface QueueForPractitionersInformService {

    /**
     * Пациент добавлен в очередь в какой-либо кабинет
     */
    fun patientAddedToOfficeQueue(patientId: String, officeId: String, onum: Int, estDuration: Int)

    /**
     * Пациент пакинул очередь в какой-либо кабинет
     */
    fun patientDeletedFromOfficeQueue(patientId: String, officeId: String)

    /**
     * Пацент добавлен в очередь к опр. врачу (к отв)
     */
    fun patientAddedToPractitionerQueue(patientId: String, practitionerId: String, observationType: String)

    /**
     * Пацент ушел из очереди к опр. врачу (к отв)
     */
    fun patientDeletedFromPractitionerQueue(patientId: String, practitionerId: String)

    /**
     * Пациент готов сделать набор обследований (если он в зоне, то информируем по типам обсл. - не по отв.)
     */
    fun patientIsReadyForObservations(patientId: String, observationTypes: List<String>)

    /**
     * Пациент прошел осмотр какого-то специалиста (не по отв.)
     */
    fun patientWasInspected(patientId: String, observationType: String) = patientDontNeedInspectionAnymore(patientId, listOf(observationType))

    /**
     * Пациенту больше не нужны перечисленные осмотры (не по отв.)
     */
    fun patientDontNeedInspectionAnymore(patientId: String, observationTypes: List<String>)

    /**
     * Все результаты готовы в маршрутном листе
     */
    fun resultsAreReadyInCarePlan(patientId: String, clinicalImpression: ClinicalImpression)
}