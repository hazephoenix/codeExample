package ru.digitalhospital.queueManager

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Primary


private const val PROPERTIES_PREFIX = "ru.viscur.dh.queue-manager"

private const val BASE_PACKAGE = "ru.digitalhospital.queueManager"
private const val REPOSITORY_PACKAGE = "$BASE_PACKAGE.repository"
private const val ENTITY_PACKAGE = "$BASE_PACKAGE.entities"

/**
 * Автоконфигурация менеджера очереди
 * (подробнее про механизм [тут](https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-developing-auto-configuration.html)
 */
@Configuration
@ComponentScan(BASE_PACKAGE)
@EnableAutoConfiguration
@ConditionalOnProperty(
        prefix = PROPERTIES_PREFIX,
        name = ["enabled"],
        havingValue = "true"
)
@EnableJpaRepositories(
        basePackages = [REPOSITORY_PACKAGE],
        entityManagerFactoryRef = "qmEntityManagerFactory"
)
class QueueManagerAutoconf {

    @Bean
    @Qualifier("qmDataSourceProperties")
    @ConfigurationProperties("$PROPERTIES_PREFIX.datasource")
    @Primary
    fun qmDataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean
    fun qmDataSource(): HikariDataSource {
        return qmDataSourceProperties()
                .initializeDataSourceBuilder()
                .type(HikariDataSource::class.java)
                .build();
    }


    @Bean
    fun qmEntityManagerFactory(builder: EntityManagerFactoryBuilder): LocalContainerEntityManagerFactoryBean {
        return builder
                .dataSource(qmDataSource())
                .packages(ENTITY_PACKAGE)
                .persistenceUnit("queueManager")
                .build();
    }
}