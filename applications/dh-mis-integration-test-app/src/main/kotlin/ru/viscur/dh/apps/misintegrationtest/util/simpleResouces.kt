package ru.viscur.dh.apps.misintegrationtest.util

import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.utils.SECONDS_IN_MINUTE
import java.util.*

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
        val items: List<QueueItemSimple>,
        val officeStatus: LocationStatus = LocationStatus.BUSY
) {
    override fun toString(): String {
        return "QueueOfOfficeSimple(officeId='$officeId', items=$items)"
    }
}

class QueueItemSimple(
        val status: PatientQueueStatus = PatientQueueStatus.IN_QUEUE,
        val severity: Severity? = null,
        val estDuration: Int = 10 * SECONDS_IN_MINUTE,
        val patientId: String? = null
) {
    override fun toString(): String {
        return "QueueItemSimple(status=$status, severity=$severity, estDuration=$estDuration, patientId=$patientId)"
    }
}

class CarePlanSimple(
        val severity: Severity,
        val servReqs: List<ServiceRequestSimple>
)

class ServiceRequestSimple(
        val code: String,
        val status: ServiceRequestStatus = ServiceRequestStatus.active,
        val locationId: String? = null
){
    override fun toString(): String {
        return "ServiceRequestSimple(code='$code', status=$status, locationId=$locationId)"
    }
}

data class ObservationDurationSimple(
        val code: String,
        val duration: Int
)

data class QueueHistoryOfPatientSimple(
        val status: PatientQueueStatus,
        val duration: Int,
        val officeId: String? = null
)
