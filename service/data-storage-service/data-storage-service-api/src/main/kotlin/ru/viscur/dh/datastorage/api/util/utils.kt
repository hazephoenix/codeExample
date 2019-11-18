package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.fhir.model.entity.ServiceRequest
import ru.viscur.dh.fhir.model.utils.code

/**
 * Created at 16.11.2019 17:02 by SherbakovaMA
 *
 * Вспомогательные функции
 */

/**
 * Обследования, которые проводит очередь
 * Так, очередь игнорирует непройденные назначения по моче
 */
fun List<ServiceRequest>.filterForQueue(): List<ServiceRequest> {
    return this.filter { it.code.code() !in URINE_ANALYSIS_TYPES }
}