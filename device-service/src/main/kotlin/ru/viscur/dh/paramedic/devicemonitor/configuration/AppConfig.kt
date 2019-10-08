package ru.viscur.dh.paramedic.devicemonitor.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.module.SimpleModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jms.annotation.EnableJms
import org.springframework.jms.support.converter.MappingJackson2MessageConverter
import org.springframework.jms.support.converter.MessageConverter
import org.springframework.jms.support.converter.MessageType
import ru.viscur.dh.paramedic.devicemonitor.dto.Reference
import ru.viscur.dh.paramedic.devicemonitor.jackson.ReferenceDeserializer
import ru.viscur.dh.paramedic.devicemonitor.jackson.ReferenceSerializer

/**
 * Created at 26.09.2019 11:18 by TimochkinEA
 *
 * App Spring configuration
 */
@Configuration
@EnableJms
class AppConfig {

    @Bean
    fun jacksonJmsMessageConverter(): MessageConverter {
        val converter = MappingJackson2MessageConverter()
        converter.setTargetType(MessageType.TEXT)
        converter.setTypeIdPropertyName("_type")
        return converter;
    }

    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        val module = SimpleModule()

        module.addSerializer(Reference::class.java, ReferenceSerializer())
        module.addDeserializer(Reference::class.java, ReferenceDeserializer())

        return mapper
    }
}
