package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.Config

/**
 * Created at 05.11.2019 15:00 by SherbakovaMA
 *
 * Репозиторий для настроек системы
 */
@Repository
interface ConfigRepository : CrudRepository<Config, Long> {

    fun findByCode(code: String): Config?

    fun deleteByCode(code: String)
}