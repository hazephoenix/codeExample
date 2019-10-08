package ru.digitalhospital.queueManager.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.digitalhospital.queueManager.entities.Office

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для кабинетов
 */
@Repository
interface OfficeRepository : CrudRepository<Office, Long>