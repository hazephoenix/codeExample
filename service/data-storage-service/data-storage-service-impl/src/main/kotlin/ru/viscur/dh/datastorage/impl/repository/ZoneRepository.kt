package ru.viscur.dh.datastorage.impl.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.datastorage.impl.entity.ZoneEntity

@Repository
interface ZoneRepository : CrudRepository<ZoneEntity, String>
