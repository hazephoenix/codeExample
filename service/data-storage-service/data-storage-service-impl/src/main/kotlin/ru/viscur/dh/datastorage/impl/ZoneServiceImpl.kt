package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.model.Zone
import ru.viscur.dh.datastorage.impl.entity.ZoneEntity
import ru.viscur.dh.datastorage.impl.repository.ZoneRepository
import ru.viscur.dh.datastorage.impl.service.ZoneService

@Service
class ZoneServiceImpl(private val zoneRepository: ZoneRepository) : ZoneService {
    override fun findAll() = zoneRepository.findAll().map(ZoneEntity::zone)

    override fun findAllById(ids: Iterable<String>) = zoneRepository.findAllById(ids).map(ZoneEntity::zone)

    override fun findById(zoneId: String) = zoneRepository.findById(zoneId).orElseGet { null }?.zone()

    override fun create(zone: Zone): Zone {
        return zoneRepository.save(zone.entity()).zone()
    }

    override fun update(zone: Zone) {
        zoneRepository.findById(zone.zoneId).orElseThrow { IllegalStateException("Zone not found") }
        zoneRepository.save(zone.entity())
    }

    override fun delete(zoneId: String) {
        val zone = zoneRepository.findById(zoneId).orElseThrow { IllegalStateException("Zone not found") }
        zoneRepository.delete(zone)
    }
}