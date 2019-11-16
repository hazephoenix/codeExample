package ru.viscur.dh.queue.impl.config

import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean

private const val PROPERTIES_PREFIX = "ru.viscur.dh.queue-manager-service"

private const val BASE_PACKAGE = "ru.viscur.dh.queue.impl"
private const val PERSISTENCE_PACKAGE = "ru.viscur.dh.queue.impl.persistence"
private const val REPOSITORY_PACKAGE = "$PERSISTENCE_PACKAGE.repository"
private const val ENTITY_PACKAGE = "$PERSISTENCE_PACKAGE.model"

/**
 * Автоконфигурация менеджера очереди
 * (подробнее про механизм [тут](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html)
 */
@Configuration
@ComponentScan(BASE_PACKAGE)
@AutoConfigureAfter(name = ["ru.viscur.dh.datastorage.impl.config.DataStorageConfig"])
class QueueManagerConfig {


}