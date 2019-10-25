package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.CodeMap
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 23.10.2019 11:03 by SherbakovaMA
 *
 * Сервис для [CodeMap]
 */
interface CodeMapService {

    /**
     * Получение [CodeMap]
     */
    fun codeMap(sourceValueSet: ValueSetName, targetValueSet: ValueSetName, sourceCode: String): CodeMap

    /**
     * Получение списка [CodeMap]
     */
    fun all(sourceValueSet: ValueSetName, targetValueSet: ValueSetName): List<CodeMap>

    /**
     * По коду диагноза [ValueSetName.ICD_10] коды из [ValueSetName.PRACTITIONER_QUALIFICATIONS]
     */
    fun icdToPractitionerQualifications(sourceCode: String) = codeMap(
            ValueSetName.ICD_10,
            ValueSetName.PRACTITIONER_QUALIFICATIONS,
            sourceCode
    ).targetCode

    /**
     * Все сопоставления диагноза [ValueSetName.ICD_10] к специальностям [ValueSetName.PRACTITIONER_QUALIFICATIONS]
     */
    fun allIcdToPractitionerQualifications() = all(
            ValueSetName.ICD_10,
            ValueSetName.PRACTITIONER_QUALIFICATIONS
    )

    /**
     * По коду диагноза [ValueSetName.ICD_10] коды услуг для маршрутного листа из [ValueSetName.OBSERVATION_TYPES]
     */
    fun icdToObservationTypes(sourceCode: String) = codeMap(
            ValueSetName.ICD_10,
            ValueSetName.OBSERVATION_TYPES,
            sourceCode
    ).targetCode.map { it.code }

    /**
     * Все сопоставления диагноза [ValueSetName.ICD_10] к услугам для маршрутного листа [ValueSetName.OBSERVATION_TYPES]
     */
    fun allIcdToObservationTypes() = all(
            ValueSetName.ICD_10,
            ValueSetName.OBSERVATION_TYPES
    )

    /**
     * По коду специальности ответственного врача [ValueSetName.PRACTITIONER_QUALIFICATIONS] код выполняемой услуги из [ValueSetName.OBSERVATION_TYPES]
     * Берется первый элемент, т к должно быть один-к-одному. Ответсвенный врач может делать только один тип осмотра
     */
    fun respQualificationToObservationTypes(sourceCode: String) = codeMap(
            ValueSetName.PRACTITIONER_QUALIFICATIONS,
            ValueSetName.OBSERVATION_TYPES,
            sourceCode
    ).targetCode.map { it.code }.first()

    /**
     * Все сопоставления специальностей ответственных врачей [ValueSetName.PRACTITIONER_QUALIFICATIONS] к коду выполняемой услуги [ValueSetName.OBSERVATION_TYPES]
     */
    fun allRespQualificationToObservationTypes() = all(
            ValueSetName.PRACTITIONER_QUALIFICATIONS,
            ValueSetName.OBSERVATION_TYPES
    )
}