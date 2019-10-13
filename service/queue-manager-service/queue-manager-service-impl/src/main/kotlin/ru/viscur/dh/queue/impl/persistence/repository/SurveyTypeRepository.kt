package ru.viscur.dh.queue.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.queue.impl.persistence.model.SurveyTypePE

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для типов обследования
 */
@Repository
interface SurveyTypeRepository : CrudRepository<SurveyTypePE, Long>
