package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Concept
import ru.viscur.dh.fhir.model.type.CodeableConcept
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 16.10.2019 17:52 by SherbakovaMA
 *
 * Сервис для работы с [Concept]
 */
interface ConceptService {

    /**
     * Получение по [CodeableConcept]
     */
    fun byCodeableConcept(codeableConcept: CodeableConcept): Concept

    /**
     * Получение родителя у [concept]
     */
    fun parent(concept: Concept): Concept?

    /**
     * Поиск концепта по коду
     * @param valueSet ValueSet, где искать, [ru.viscur.dh.fhir.model.valueSets.ValueSetName]
     * @param code значение кода
     */
    fun byCode(valueSet: ValueSetName, code: String): Concept

    /**
     * По коду родителя
     * при незаданном parentCode ищет корневые элементы
     */
    fun byParent(valueSet: ValueSetName, parentCode: String? = null): List<Concept>

    /**
     * Все коды последнего уровня (не имеющие дочерних элементов)
     */
    fun allInLastLevel(valueSet: ValueSetName): List<String>

    /**
     * Поиск концепта по совпадениям в [Concept.alternatives]
     * или со значением display
     *
     * Пример:
     * В [realAlternatives] м б "Выраженная СИЛЬНАЯ боль по утрам"
     * В коде Violent_pain (острая боль) указаны альтернативы "острая боль", "сильная боль"
     * В результате будет найден код "Violent_pain"
     */
    fun byAlternativeOrDisplay(valueSet: ValueSetName, realAlternatives: List<String>): List<String>
}