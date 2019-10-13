package ru.viscur.dh.queue.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.queue.impl.persistence.model.OfficePE

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для кабинетов
 */
@Repository
interface OfficeRepository : CrudRepository<OfficePE, Long>