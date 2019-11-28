package ru.viscur.dh.integration.mis.api

import ru.viscur.dh.fhir.model.entity.Observation

/**
 * Created at 22.11.2019 16:36 by SherbakovaMA
 *
 * Сервис для обработки запросов по проведению обследований в МИС
 * Обследование рассматривается не как ресурс, а как проведение обследования по назначению в маршрутном листе
 */
interface ObservationInCarePlanService {

    /**
     * Создать обследование
     * Обследование проведено по назначению в маршрутном листе
     */
    fun create(observation: Observation): Observation

    /**
     * Обновить обследование -
     * Внести результаты обследования
     */
    fun update(observation: Observation): Observation
}