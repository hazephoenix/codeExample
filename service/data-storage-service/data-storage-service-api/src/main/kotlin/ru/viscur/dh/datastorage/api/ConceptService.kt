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
     * @param valueSetId id ValueSet-а, где искать, [ru.viscur.dh.fhir.model.valueSets.ValueSetName]
     * @param code значение кода
     */
    fun byCode(valueSetId: String, code: String): Concept

    /**
     * По коду родителя
     * при незаданном parentCode ищет корневые элементы
     */
    fun byParent(valueSet: ValueSetName, parentCode: String? = null): List<Concept>

    /**
     * Поиск концепта по совпадениям в [Concept.alternatives]
     * Пример:
     * В [realAlternatives] м б "Выраженная СИЛЬНАЯ боль по утрам"
     * В коде Violent_pain (острая боль) указаны альтернативы "острая боль", "сильная боль"
     * В результате будет найден код "Violent_pain"
     */
    fun byAlternative(valueSetId: String, realAlternatives: List<String>): List<String>
}