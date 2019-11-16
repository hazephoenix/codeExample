package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.ObservationDefaultDuration

/**
 * Created at 05.11.2019 15:00 by SherbakovaMA
 *
 * Репозиторий для [ObservationDefaultDuration]
 */
@Repository
interface ObservationDefaultDurationRepository : CrudRepository<ObservationDefaultDuration, Long> {

    /**
     * Поиск значения по коду услуги, диагнозу, степени тяжести
     */
    fun findFirstByCodeIsAndDiagnosisIsAndSeverityIs(code: String, diagnosis: String, severity: String): ObservationDefaultDuration?

    /**
     * Поиск значения по коду услуги, степени тяжести
     */
    fun findFirstByCodeIsAndSeverityIs(code: String, severity: String): ObservationDefaultDuration?
}