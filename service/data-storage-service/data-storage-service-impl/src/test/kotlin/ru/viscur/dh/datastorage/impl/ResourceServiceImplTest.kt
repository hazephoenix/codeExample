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
import ru.viscur.dh.fhir.model.enums.ResourceType

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
        assertEquals("creating resource", created!!.name)

        val updateSource = HealthcareService(
                id = created.id!!,
                name = "updating resource",
                type = listOf(),
                location = listOf()
        )
        val updated = resourceServiceImpl.update(updateSource)
        assertNotNull(updated)
        assertEquals(created.id, updated?.id)
        assertNotSame(updateSource, updated)
        assertEquals("updating resource", updated?.name)
    }

    @Test
    fun `should return resource by id when exists`() {
        val location = resourceServiceImpl.byId(ResourceType.Location, "Location/139");
        assertNotNull(location)
        assertEquals("Location/139", location!!.id)
    }

    @Test
    fun `should return null when resource with id doesn't exists`() {
        val location = resourceServiceImpl.byId(ResourceType.Location, "Location/Unknown");
        assertNull(location)
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
        val deleted = resourceServiceImpl.deleteById(ResourceType.HealthcareService, created?.id!!)
        assertNotNull(deleted)
        assertEquals(created.id, deleted!!.id)
        assertNull(resourceServiceImpl.byId(ResourceType.HealthcareService, created?.id!!))
    }
}