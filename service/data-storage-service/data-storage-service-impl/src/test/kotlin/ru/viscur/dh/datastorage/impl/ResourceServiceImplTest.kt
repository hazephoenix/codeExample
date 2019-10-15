package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.DataStorageConfig
import ru.viscur.dh.fhir.model.entity.HealthcareService

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
        TODO("Implement me")
    }

    @Test
    fun `should return null when resource with id doesn't exists`() {
        TODO("Implement me")
    }

    @Test
    fun `should return all resources by resource type`() {
        TODO("Implement me")
    }


}