package ru.viscur.dh.datastorage.impl.config


import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.configuration.FluentConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.*
import org.springframework.context.annotation.ComponentScan.Filter
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import javax.persistence.EntityManagerFactory

const val PERSISTENCE_UNIT_NAME = "datastoragePersistenceUnit"

private const val PROPERTIES_PREFIX = "ru.viscur.dh.data-storage"

private const val BASE_PACKAGE = "ru.viscur.dh.datastorage.impl"
private const val REPOSITORY_PACKAGE = "$BASE_PACKAGE.repository"
private const val ENTITY_PACKAGE = "$BASE_PACKAGE.entities"



@Configuration
@ComponentScan(
        basePackages = ["ru.viscur.dh.datastorage.impl"],
        excludeFilters = [
            Filter(
                    pattern = [
                        "ru/viscur/dh/datastorage/impl/config/DataStorageConfig"
                    ],
                    type = FilterType.REGEX
            )
        ]
)
@EnableTransactionManagement
@EnableAutoConfiguration
class DataStorageConfig {

    init {
        String::class.java.name
    }

    @Bean(name = ["dsDataSourceProperties"])
    @ConfigurationProperties("$PROPERTIES_PREFIX.datasource")
    @Primary
    fun dataSourceProperties(): DataSourceProperties = DataSourceProperties()

    @Bean(name = ["dsDataSource"])
    fun dataSource() = dataSourceProperties()
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build()!!

    @Bean(name = ["dsFlyway"], initMethod = "migrate")
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
    @DependsOn("dsFlyway")
    fun entityManagerFactory(builder: EntityManagerFactoryBuilder) =
            builder
                    .dataSource(dataSource())
                    .packages(ENTITY_PACKAGE)
                    .properties(mapOf("hibernate.dialect" to "ru.viscur.dh.datastorage.impl.config.PostgreSQL95JsonDialect"))
                    .persistenceUnit(PERSISTENCE_UNIT_NAME)
                    .build()!!

    @Bean(name = ["dsTxManager"])
    fun txManager(@Qualifier("dsEntityManagerFactory") dsEntityManagerFactory: EntityManagerFactory) = JpaTransactionManager(dsEntityManagerFactory)

}