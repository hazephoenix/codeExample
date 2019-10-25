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
    fun codeMap(sourceValueSetId: String, targetValueSetId: String, sourceCode: String): CodeMap

    /**
     * По коду диагноза [ValueSetName.ICD_10] коды из [ValueSetName.PRACTITIONER_QUALIFICATIONS]
     */
    fun icdToPractitionerQualifications(sourceCode: String) = codeMap(
            ValueSetName.ICD_10.id,
            ValueSetName.PRACTITIONER_QUALIFICATIONS.id,
            sourceCode
    ).targetCode

    /**
     * По коду диагноза [ValueSetName.ICD_10] коды услуг для маршрутного листа из [ValueSetName.OBSERVATION_TYPES]
     */
    fun icdToObservationTypes(sourceCode: String) = codeMap(
            ValueSetName.ICD_10.id,
            ValueSetName.OBSERVATION_TYPES.id,
            sourceCode
    ).targetCode.map { it.code }

    /**
     * По коду специальности ответственного врача [ValueSetName.PRACTITIONER_QUALIFICATIONS] код выполняемой услуги из [ValueSetName.OBSERVATION_TYPES]
     * Берется первый элемент, т к должно быть один-к-одному. Ответсвенный врач может делать только один тип осмотра
     */
    fun respQualificationToObservationTypes(sourceCode: String) = codeMap(
            ValueSetName.PRACTITIONER_QUALIFICATIONS.id,
            ValueSetName.OBSERVATION_TYPES.id,
            sourceCode
    ).targetCode.map { it.code }.first()
}