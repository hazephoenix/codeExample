package ru.digitalhospital.dhdatastorage.utils

import com.vladmihalcea.hibernate.type.json.JsonNodeBinaryType
import org.hibernate.dialect.PostgreSQL95Dialect
import java.sql.Types

/**
 * Created at 30.09.2019 16:15 by SherbakovaMA
 *
 * Диалект PostgreSQL с поддержкой json
 */
class PostgreSQL95JsonDialect : PostgreSQL95Dialect() {
    init {
        this.registerHibernateType(
                Types.OTHER, JsonNodeBinaryType::class.java.name
        )
    }
}