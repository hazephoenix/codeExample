package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.*
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.utils.*
import ru.viscur.dh.fhir.model.valueSets.*

/**
 * Репозиторий для [TrainingSample]
 */
@Repository
interface TrainingSampleRepository: CrudRepository<TrainingSample, Long> {


}