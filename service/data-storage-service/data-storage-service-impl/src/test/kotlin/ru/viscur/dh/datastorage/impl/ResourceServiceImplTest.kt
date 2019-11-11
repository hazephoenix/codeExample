package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.DataStorageConfig
import ru.viscur.dh.fhir.model.entity.HealthcareService
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import java.util.concurrent.CompletableFuture

@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only")
class ResourceServiceImplTest {
    @Autowired
    lateinit var resourceServiceImpl: ResourceService

    @Test
    @Order(1)
    fun `should create & update resource`() {
        val source = HealthcareService(
                name = "creating resource",
                type = listOf(),
                location = listOf()
        )
        val created = resourceServiceImpl.create(source)
        assertNotNull(created)
        assertNotSame(source, created)
        assertEquals("creating resource", created.name)

        val updated = resourceServiceImpl.update(ResourceType.HealthcareService, created.id) {
            id = created.id
            name = "updating resource"
            type = listOf()
            location = listOf()
        }
        assertNotNull(updated)
        assertEquals(created.id, updated.id)
        assertEquals("updating resource", updated.name)
        assertEquals("updating resource", updated.name)
    }

    @Test
    @Order(1)
    fun `should create with id`() {
        val initId = "init_id"
        val created = resourceServiceImpl.create(HealthcareService(
                id = initId,
                name = "creating resource",
                type = listOf(),
                location = listOf()
        ))
        val read = resourceServiceImpl.byId(ResourceType.HealthcareService, initId)
        assertEquals(initId, read.id)
    }

    @Test
    fun `should return resource by id when exists`() {
        val location = resourceServiceImpl.byId(ResourceType.Location, "Office:139")
        assertNotNull(location)
        assertEquals("Office:139", location.id)
    }

    @Test
    fun `should throw exception when resource with id doesn't exists`() {
        assertThrows(Exception::class.java) { resourceServiceImpl.byId(ResourceType.Location, "Location/Unknown") }
    }

    @Test
    fun `should throw exception while deleting when resource with id doesn't exists`() {
        assertThrows(Exception::class.java) { resourceServiceImpl.deleteById(ResourceType.Location, "Location/Unknown") }
    }

    @Test
    @Order(2)
    fun `should delete rows by name`() {
        val deletedCount = resourceServiceImpl.deleteAll(ResourceType.HealthcareService, RequestBodyForResources(
                mapOf("name" to "updating resource")
        ))
        assertTrue(deletedCount > 0)
    }

    @Test
    fun `should return all resources by resource type and name`() {
        val found = resourceServiceImpl.all(
                ResourceType.Location,
                RequestBodyForResources(
                        mapOf(
                                "name" to "мотров"
                        ),
                        true,
                        listOf("id desc")
                )
        )
        assertNotNull(found)
        assertEquals(3, found.size)
    }

    @Test
    fun `should delete resource by id`() {
        val source = HealthcareService(
                name = "deleting resource",
                type = listOf(),
                location = listOf()
        )
        val created = resourceServiceImpl.create(source)
        assertNotNull(created)
        val deleted = resourceServiceImpl.deleteById(ResourceType.HealthcareService, created.id)
        assertNotNull(deleted)
        assertEquals(created.id, deleted.id)
        assertThrows(Exception::class.java) { resourceServiceImpl.byId(ResourceType.HealthcareService, created.id) }
    }

    @Test
    fun updateResource() {
        val id = "for test"
        try {
            resourceServiceImpl.create(Location(id = id, name = "1"))
            val updated = resourceServiceImpl.update(ResourceType.Location, id) {
                name = "newName"
            }
            assertEquals("newName", updated.name)
        } finally {
            resourceServiceImpl.deleteById(ResourceType.Location, id)
        }
    }

    @Test
    fun nestingUpdatingResource() {
        val locationId = "for test. location"
        val hsId = "for test. hs"
        try {
            resourceServiceImpl.create(Location(id = locationId, name = "1"))
            resourceServiceImpl.create(HealthcareService(
                    id = hsId,
                    name = "1",
                    type = listOf(),
                    location = listOf()
            ))

            resourceServiceImpl.update(ResourceType.Location, locationId) {
                name = "newName location"
                resourceServiceImpl.update(ResourceType.HealthcareService, hsId) {
                    name = "newName hs"
                }
            }
            assertEquals("newName location", resourceServiceImpl.byId(ResourceType.Location, locationId).name)
            assertEquals("newName hs", resourceServiceImpl.byId(ResourceType.HealthcareService, hsId).name)
        } finally {
            resourceServiceImpl.deleteById(ResourceType.Location, locationId)
            resourceServiceImpl.deleteById(ResourceType.HealthcareService, hsId)
        }
    }

    @Test
    fun severalUpdatingResourceInTx() {

        val id = "for test severalUpdatingResourceInTx"
        try {
            resourceServiceImpl.create(Location(id = id, name = "20"))
            updateLocation(id)
            val locationAfterUpdated = resourceServiceImpl.byId(ResourceType.Location, id)
            assertEquals("21", locationAfterUpdated.name)
            assertEquals(LocationStatus.OBSERVATION, locationAfterUpdated.status)
        } finally {
            resourceServiceImpl.deleteById(ResourceType.Location, id)
        }
    }

    @Tx
    private fun updateLocation(id: String) {
        resourceServiceImpl.update(ResourceType.Location, id) {
            val prevName = name.toInt()
            name = (prevName + 1).toString()
        }
        resourceServiceImpl.update(ResourceType.Location, id) {
            if (name.length > 0) {
                status = LocationStatus.OBSERVATION
            }
        }
    }

    @Test
    fun asyncUpdateResource() {
        val id = "for async test"
        try {
            resourceServiceImpl.create(Location(id = id, name = "1"))
            for (i in (1..100)) {
                CompletableFuture.runAsync {
                    resourceServiceImpl.update(ResourceType.Location, id) {
                        val prevName = name.toInt()
                        name = (prevName + 1).toString()
                    }
                }
            }
            Thread.sleep(5000)//чтобы потоки успели завершиться
            val locationAfterUpdated = resourceServiceImpl.byId(ResourceType.Location, id)
            assertEquals("101", locationAfterUpdated.name)
        } finally {
            resourceServiceImpl.deleteById(ResourceType.Location, id)
        }
    }
}