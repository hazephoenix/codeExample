package ru.viscur.dh.apps.centralserver.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

/**
 * Created at 11.11.2019 15:08 by TimochkinEA
 *
 * Настройки JMS
 */
@Configuration
@EnableJms
class JmsConfig(
        private val jackson2ObjectMapperBuilder: Jackson2ObjectMapperBuilder
) {

    @Bean
    fun jacksonJmsMessageConverter(): MessageConverter {
        val objectMapper = jackson2ObjectMapperBuilder.build<ObjectMapper>().apply {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
        val converter = MappingJackson2MessageConverter()
        converter.setObjectMapper(objectMapper)
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter
    }
}
