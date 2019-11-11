package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.ObservationDurationHistory

/**
 * Created at 05.11.2019 15:00 by SherbakovaMA
 *
 * Репозиторий для [ObservationDurationHistory]
 */
@Repository
interface ObservationDurationHistoryRepository : CrudRepository<ObservationDurationHistory, Long> {

    @Query(value = "SELECT avg(duration) FROM observation_duration_history where " +
            "code = :code and diagnosis = :diagnosis and severity = :severity", nativeQuery = true)
    fun avgByAll(
            code: String, diagnosis: String, severity: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM observation_duration_history where " +
            "code = :code and diagnosis = :diagnosis", nativeQuery = true)
    fun avgByCodeAndDiagnosis(
            code: String, diagnosis: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM observation_duration_history where " +
            "code = :code and severity = :severity", nativeQuery = true)
    fun avgByCodeAndSeverity(
            code: String, severity: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM observation_duration_history where " +
            "code = :code", nativeQuery = true)
    fun avgByCode(
            code: String
    ): Double?
}