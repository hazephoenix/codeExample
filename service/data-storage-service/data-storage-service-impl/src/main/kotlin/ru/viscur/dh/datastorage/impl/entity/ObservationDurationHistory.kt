package ru.viscur.dh.datastorage.impl.entity

import com.fasterxml.jackson.annotation.JsonCreator
import ru.viscur.dh.fhir.model.enums.Severity
import javax.persistence.*

/**
 * Created at 06.11.2019 11:34 by SherbakovaMA
 *
 * История(статистика) продолжительности проведения обследований/услуг
 *
 * @param code код услуги из [ru.viscur.dh.fhir.model.valueSets.ValueSetName.OBSERVATION_TYPES]
 * @param diagnosis код диагноза из [ru.viscur.dh.fhir.model.valueSets.ValueSetName.ICD_10]
 * @param severity степень тяжести пациента, [Severity]
 * @param duration продолжительность, в секундах
 */
@Entity
@Table(name = "observation_duration_history")
@SequenceGenerator(name = "pk_seq", sequenceName = "pk_seq", allocationSize = 1)
data class ObservationDurationHistory(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "pk_seq")
        var id: Long = 0L,
        @Column
        var code: String? = null,
        @Column
        var diagnosis: String? = null,
        @Column
        var severity: String? = null,
        @Column
        var duration: Int? = null
)