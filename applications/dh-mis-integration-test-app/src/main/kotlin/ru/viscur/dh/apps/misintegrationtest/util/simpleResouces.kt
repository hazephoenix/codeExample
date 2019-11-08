package ru.viscur.dh.apps.misintegrationtest.util

import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE

/**
 * Created at 08.11.2019 11:39 by SherbakovaMA
 *
 * Объекты для тестов: необходимые dto и упрощенные ресурсы
 */

/**
 * Рассматриваемый кэйз теста
 * @param desc описание
 * @param queue очередь на момент проверки
 * @param carePlan маршрутный лист пациента
 */
open class BaseTestCase(
        val desc: String,
        val queue: List<QueueOfOfficeSimple>,
        val carePlan: CarePlanSimple
)

class QueueOfOfficeSimple(
        val officeId: String,
        val items: List<QueueItemSimple>
)

class QueueItemSimple(
        val status: PatientQueueStatus,
        val severity: Severity,
        val estDuration: Int = 10 * SECONDS_IN_MINUTE
)

class CarePlanSimple(
        val severity: Severity,
        val servReqs: List<ServiceRequestSimple>
)

class ServiceRequestSimple(
        val code: String,
        val status: ServiceRequestStatus = ServiceRequestStatus.active
)
