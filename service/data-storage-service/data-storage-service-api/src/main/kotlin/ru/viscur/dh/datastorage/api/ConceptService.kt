package ru.viscur.dh.datastorage.api

import ru.viscur.dh.fhir.model.entity.Concept
import ru.viscur.dh.fhir.model.type.CodeableConcept

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
}