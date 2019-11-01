package ru.viscur.dh.integration.mis.rest.api

import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.ServiceRequest

/**
 * Created at 01.11.2019 11:28 by SherbakovaMA
 *
 * Сервис для обработки запросов подсистемы "АРМ Фельдшер"
 */
interface ReceptionService {

    /**
     * Регистрация обращения пациента:
     * Сохранение всех данных, полученных на АРМ Фельдшер
     * Регистрация в очереди
     */
    fun registerPatient(bundle: Bundle): List<ServiceRequest>
}