package ru.viscur.dh.datastorage.impl

import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ConfigService
import ru.viscur.dh.datastorage.impl.entity.Config
import ru.viscur.dh.datastorage.impl.repository.ConfigRepository
import ru.viscur.dh.transaction.desc.config.annotation.Tx

/**
 * Created at 05.11.2019 14:54 by SherbakovaMA
 */
@Service
@EnableJpaRepositories(entityManagerFactoryRef = "dsEntityManagerFactory", transactionManagerRef = "dsTxManager")
class ConfigServiceImpl(
        private val configRepository: ConfigRepository
) : ConfigService {

    /**
     * Закешированные настройки
     * code - value
     */
    private var configs: Map<String, String>? = null

    override fun read(code: String): String? {
        configs ?: run { readValuesFromDb() }
        return configs?.let { it[code] }
    }

    @Tx
    override fun write(code: String, value: String?) {
        value?.run {
            val config = configRepository.findByCode(code)?.apply { this.value = value }
                    ?: Config(code = code, value = value)
            configRepository.save(config)
        } ?: run {
            configRepository.deleteByCode(code)
        }
        configs = null
    }

    private fun readValuesFromDb() {
        configs = configRepository.findAll().associate { it.code!! to it.value!! }
    }
}