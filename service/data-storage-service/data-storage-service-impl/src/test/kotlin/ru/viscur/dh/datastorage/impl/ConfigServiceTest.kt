package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import ru.viscur.dh.datastorage.api.ConfigService
import ru.viscur.dh.datastorage.impl.config.DataStorageConfig

/**
 * Created at 05.11.2019 15:22 by SherbakovaMA
 *
 * Тест для [ConfigService]
 */
@Disabled("Debug purposes only")
@SpringBootTest(
        classes = [DataStorageConfig::class]
)
@EnableAutoConfiguration
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory", transactionManagerRef = "dsTxManager")
class ConfigServiceTest {

    @Autowired
    lateinit var configService: ConfigService

    companion object {
        private const val code1 = "Test config"
        private const val code2 = "Test config2"
    }

    @Test
    fun `create config`() {
        val value = "test"
        configService.write(code1, value)
        assertEquals(value, configService.read(code1))
        configService.delete(code1)
    }

    @Test
    fun `update config`() {
        var value = "test"
        configService.write(code1, value)
        assertEquals(value, configService.read(code1))
        value = "test2"
        configService.write(code1, value)
        assertEquals(value, configService.read(code1))
        configService.delete(code1)
    }

    @Test
    fun `delete config`() {
        val value = "test"
        configService.write(code1, value)
        configService.delete(code1)
        assertNull(configService.read(code1))
    }

    @Test
    fun `multiple updating configs`() {
        val value1 = "test1"
        val value2 = "test2"
        configService.write(code1, value1)
        configService.write(code2, value2)
        assertEquals(value1, configService.read(code1))
        assertEquals(value2, configService.read(code2))
        configService.delete(code1)
        configService.delete(code2)
    }
}