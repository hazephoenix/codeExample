package ru.viscur.dh.datastorage.impl.config


import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.*
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.task.SimpleAsyncTaskExecutor
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.scheduling.TaskScheduler
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.concurrent.Executor
import javax.persistence.EntityManagerFactory

const val PERSISTENCE_UNIT_NAME = "datastoragePersistenceUnit"

private const val PROPERTIES_PREFIX = "ru.viscur.dh.data-storage"

private const val BASE_PACKAGE = "ru.viscur.dh.datastorage.impl"
private const val REPOSITORY_PACKAGE = "$BASE_PACKAGE.repository"
private const val ENTITY_PACKAGE = "$BASE_PACKAGE.entity"


@Configuration
@ComponentScan(
        basePackages = ["ru.viscur.dh.datastorage.impl"],
        excludeFilters = [
            Filter(
                    /*pattern = [
                        "ru\\.viscur\\.dh\\.datastorage\\.impl\\.config\\.."
                    ],*/
                    type = FilterType.REGEX
            )
        ]
)
@EnableTransactionManagement
@EnableConfigurationProperties
@AutoConfigureAfter(HibernateJpaAutoConfiguration::class)
class DataStorageConfig {

    @Bean
    @Primary
    fun flywayMigrationStrategy(): FlywayMigrationStrategy {
        return FlywayMigrationStrategy {
            /* nothing to do */
        }
    }

    @Bean(name = ["dsDataSourceProperties"])
    @ConfigurationProperties("$PROPERTIES_PREFIX.datasource")
    @Primary
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean(name = ["dsDataSource"])
    fun dataSource() = dataSourceProperties()
            .initializeDataSourceBuilder()
            .driverClassName("org.postgresql.Driver")
            .type(HikariDataSource::class.java)
            .build()!!

    @Bean(name = ["dsFlyway"], initMethod = "migrate")
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun flyway() = Flyway(
            FluentConfiguration()
                    .dataSource(dataSource())
                    .installedBy("Migrate-On-StartUp")
                    .placeholderReplacement(true)
                    .placeholders(
                            mapOf("owner" to dataSource().username)
                    )
                    .locations(
                            "classpath:ru.viscur.dh.datastorage.db.migration"
                    )
    )

    @Bean(name = ["dsEntityManagerFactory"])
    fun entityManagerFactory(builder: EntityManagerFactoryBuilder) =
            builder
                    .dataSource(dataSource())
                    .packages(ENTITY_PACKAGE)
                    .properties(mapOf("hibernate.dialect" to "ru.viscur.dh.datastorage.impl.config.PostgreSQL95JsonDialect"))
                    .persistenceUnit(PERSISTENCE_UNIT_NAME)
                    .build()!!

    @Bean(name = ["dsTxManager"])
    fun txManager(@Qualifier("dsEntityManagerFactory") dsEntityManagerFactory: EntityManagerFactory) = JpaTransactionManager(dsEntityManagerFactory)

    @Bean
    fun taskExecutor(): Executor {
        return SimpleAsyncTaskExecutor()
    }

    @Bean
    fun taskScheduler(): TaskScheduler {
        return ConcurrentTaskScheduler()
    }
}