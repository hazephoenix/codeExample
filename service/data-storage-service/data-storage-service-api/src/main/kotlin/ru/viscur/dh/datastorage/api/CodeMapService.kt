package ru.viscur.dh.datastorage.api

import ru.viscur.dh.datastorage.api.response.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 23.10.2019 11:03 by SherbakovaMA
 *
 * Сервис для [CodeMap]
 */
interface CodeMapService {

    /**
     * see [codeMapNullable]
     * если не найден код - падение
     */
    fun codeMap(sourceValueSet: ValueSetName, targetValueSet: ValueSetName, sourceCode: String): CodeMap =
            codeMapNullable(sourceValueSet, targetValueSet, sourceCode)
                    ?: throw Exception("not found codeMap for sourceCode: $sourceCode (sourceValueSet: $sourceValueSet, targetValueSet: $targetValueSet)")

    /**
     * Получение [CodeMap]
     * В [sourceCode] можно указывать любой дочерний элемент.
     * например, для диагноз A50.9 найдется CodeMap для A50-A64, т к A50.9 входит в A50-A64
     */
    fun codeMapNullable(sourceValueSet: ValueSetName, targetValueSet: ValueSetName, sourceCode: String): CodeMap?

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
    fun icdToObservationTypes(sourceCode: String) = codeMapNullable(
            ValueSetName.ICD_10,
            ValueSetName.OBSERVATION_TYPES,
            sourceCode
    )?.targetCode?.map { it.code }

    /**
     * Все сопоставления диагноза [ValueSetName.ICD_10] к услугам для маршрутного листа [ValueSetName.OBSERVATION_TYPES]
     */
    fun allIcdToObservationTypes() = all(
            ValueSetName.ICD_10,
            ValueSetName.OBSERVATION_TYPES
    )

    /**
     * Найти список кодов жалоб, соответствующих коду МКБ-10
     */
    fun icdToComplaints(sourceCode: String) = codeMapNullable(
            ValueSetName.ICD_10,
            ValueSetName.COMPLAINTS,
            sourceCode
    )?.targetCode?.map { it.code }

    /**
     * Найти код диагноза по всем жалобам из списка
     *
     * @param complaints Список кодов жалоб, найденных в справочнике и соответсвующих тем,
     *  что ввел фельдшер
     * @param take Сколько подходящих записей необходимо найти
     * @return List<String> Список кодов МКБ, подходящих по списку жалоб
     */
    fun icdByAllComplaints(complaints: List<String>, take: Int): List<String>

    /**
     * Найти код диагноза по любым из жалоб из списка
     *
     * @param complaints Список кодов жалоб, найденных в справочнике и соответсвующих тем,
     *  что ввел фельдшер
     * @param take Сколько подходящих записей необходимо найти
     * @return Список кодов МКБ, подходящих по списку жалоб, и число вхождений искомых жалоб
     */
    fun icdByAnyComplaints(complaints: List<String>, take: Int): List<ComplaintOccurrence?>

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
