package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.ReaderEventLogEntity

@Repository
interface ReaderEventRepository : CrudRepository<ReaderEventLogEntity, Long>
