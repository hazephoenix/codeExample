package ru.viscur.dh.apps.rfidlocationdevice

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType

@Configuration
@EnableJms
class RfidLocationDeviceAppConfiguration(
        private val jackson2ObjectMapperBuilder: Jackson2ObjectMapperBuilder
) {

    @Bean
    fun jacksonJmsMessageConverter(): MessageConverter = MappingJackson2MessageConverter().apply {
        val objectMapper = jackson2ObjectMapperBuilder.build<ObjectMapper>().apply {
            configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        }
        setObjectMapper(objectMapper)
        setTargetType(MessageType.TEXT)
        setTypeIdPropertyName("_type")
    }
}
