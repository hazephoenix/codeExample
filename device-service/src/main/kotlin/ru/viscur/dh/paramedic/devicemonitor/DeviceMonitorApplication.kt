package ru.viscur.dh.paramedic.devicemonitor

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.jms.core.JmsTemplate
import ru.viscur.dh.paramedic.devicemonitor.configuration.AppUID
import javax.annotation.PostConstruct

@SpringBootApplication
class DeviceMonitorApplication {

    @Autowired
    private lateinit var template: JmsTemplate

    @Autowired
    private lateinit var uid: AppUID

    @PostConstruct
    fun postCreate() {
/*
        val queue = "${uid.uid}-paramedic-requests"
        val node = ObjectMapper().createObjectNode()
        node.put("type", "arterial pressure")
        template.convertAndSend(queue, node)
*/
    }
}

fun main(args: Array<String>) {
    runApplication<DeviceMonitorApplication>(*args)
}
