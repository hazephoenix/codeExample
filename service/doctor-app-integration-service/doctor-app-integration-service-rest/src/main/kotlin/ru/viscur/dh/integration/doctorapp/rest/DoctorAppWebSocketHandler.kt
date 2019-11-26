package ru.viscur.dh.integration.doctorapp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.viscur.dh.integration.doctorapp.api.DoctorAppService
import ru.viscur.dh.integration.doctorapp.api.cmd.AcceptDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.DeclineDoctorCallCmd
import ru.viscur.dh.integration.doctorapp.api.cmd.NewDoctorCallCmd
import java.lang.Exception
import java.util.concurrent.ConcurrentHashMap


class DoctorAppWebSocketHandler(
        val doctorAppService: DoctorAppService
) : TextWebSocketHandler() {
    val objectMapper = ObjectMapper()
    val sessionsByUsers = ConcurrentHashMap<String, List<WebSocketSession>>()

    override fun afterConnectionEstablished(session: WebSocketSession) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket connection established, session id = {}", session.id)
            logger.trace("Session attributes:")
            session.attributes.forEach { (key, value) ->
                logger.trace("\t-$key: $value")
            }
        }

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket connection closed with status = {}, session id = {}", status, session.id)
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket message from session with id = {}: {}", session.id, message.payload)
        }
        /*val incomingMessage = deserializeIncomingMessage(message)
        val response = when (incomingMessage) {
            is NewDoctorCallCmd -> doctorAppService.newCall(incomingMessage)
            is AcceptDoctorCallCmd -> doctorAppService.acceptCall(incomingMessage)
            is DeclineDoctorCallCmd -> doctorAppService.declineCall(incomingMessage)
            else -> throw Exception();
        }

        if (response !is Unit) {

        }*/

    }

    private fun deserializeIncomingMessage(message: TextMessage): Any? {
        val transportMessage = objectMapper.readValue(message.payload, WsTransportIncomingMessage::class.java)
        val message = objectMapper.readValue(
                transportMessage.payload,
                transportMessage.payloadType.payloadClass.java
        )
        return message
    }

 /*   @EventListener
    public fun onEvent() {

    }*/

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DoctorAppWebSocketHandler::class.java)
    }
}