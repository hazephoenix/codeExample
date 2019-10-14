package ru.viscur.dh.datastorage.impl

import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import ru.viscur.dh.datastorage.impl.config.BootAutoconf


@RunWith(SpringRunner::class)
@SpringBootTest(
        classes = [BootAutoconf::class],
        properties = [
            "ru.viscur.dh.data-storage.enabled=true",
            "ru.viscur.dh.data-storage.datasource.url=jdbc:postgresql://localhost:5432/dh_datastorage",
            "ru.viscur.dh.data-storage.datasource.username=dh_datastorage",
            "ru.viscur.dh.data-storage.datasource.password=dh_datastorage",
            "ru.viscur.dh.data-storage.datasource.driver-class-name=org.postgresql.Driver",
            "ru.viscur.dh.data-storage.datasource.hibernate.dialect=ru.viscur.dh.datastorage.impl.config.PostgreSQL95JsonDialect"
        ]
)
@Ignore
class ResourceServiceImplTest {

    @Test
    fun `should create resource`() {
        TODO("Implement me")
    }

    @Test
    fun `should update resource`() {
        TODO("Implement me")
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