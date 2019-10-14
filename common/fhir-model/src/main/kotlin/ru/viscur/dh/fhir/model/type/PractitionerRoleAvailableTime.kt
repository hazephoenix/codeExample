package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.DayOfWeek
import java.sql.Timestamp

/**
 * Created at 03.10.2019 16:34 by SherbakovaMA
 *
 * Расписание и часы приема роли мед. работника [ru.viscur.dh.fhir.model.entity.PractitionerRole]
 *
 * @param daysOfWeek дни недели
 * @param allDay всегда доступна. Если true, то [availableStartTime] и [availableEndTime] не заполняются
 * @param availableStartTime время открытия
 * @param availableEndTime время закрытия
 */
class PractitionerRoleAvailableTime @JsonCreator constructor(
        @JsonProperty("daysOfWeek") val daysOfWeek: List<DayOfWeek>,
        @JsonProperty("allDay") val allDay: Boolean = false,
        @JsonProperty("availableStartTime") val availableStartTime: Timestamp? = null,
        @JsonProperty("availableEndTime") val availableEndTime: Timestamp? = null
)