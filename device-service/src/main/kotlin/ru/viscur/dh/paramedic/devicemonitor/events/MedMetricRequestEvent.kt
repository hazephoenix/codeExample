package ru.viscur.dh.paramedic.devicemonitor.events

import ru.viscur.dh.paramedic.devicemonitor.dto.ServiceRequest

/**
 * Created at 01.10.2019 17:40 by TimochkinEA
 */
data class MedMetricRequestEvent(val request: ServiceRequest)
