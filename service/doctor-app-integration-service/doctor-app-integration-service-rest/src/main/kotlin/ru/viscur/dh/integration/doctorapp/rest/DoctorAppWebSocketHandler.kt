package ru.viscur.dh.integration.doctorapp.rest

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.core.Authentication
import org.springframework.web.socket.CloseStatus
import org.springframework.web.socket.TextMessage
import org.springframework.web.socket.WebSocketSession
import org.springframework.web.socket.handler.TextWebSocketHandler
import ru.viscur.dh.integration.doctorapp.api.event.DoctorAppEvent
import ru.viscur.dh.security.DhUserDetails
import java.lang.Exception
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


class DoctorAppWebSocketHandler : TextWebSocketHandler() {
    private val objectMapper = ObjectMapper()
    private val sessionsByUsers = mutableMapOf<String, MutableList<WebSocketSession>>()

    private val sessionsLock = ReentrantReadWriteLock()


    override fun afterConnectionEstablished(session: WebSocketSession) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket connection established, session id = {}", session.id)
            logger.trace("Session attributes:")
            session.attributes.forEach { (key, value) ->
                logger.trace("\t-$key: $value")
            }
        }
        val principal = session.principal
        if (principal is Authentication &&
                principal.isAuthenticated &&
                principal.details is DhUserDetails
        ) {
            val user = principal.details as DhUserDetails
            sessionsLock.write {
                sessionsByUsers.getOrPut(user.id, { mutableListOf() })
                        .add(session)
            }
        } else {
            session.close(CloseStatus.SESSION_NOT_RELIABLE)
        }

    }

    override fun afterConnectionClosed(session: WebSocketSession, status: CloseStatus) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket connection closed with status = {}, session id = {}", status, session.id)
        }
        val principal = session.principal
        if (principal is Authentication &&
                principal.isAuthenticated &&
                principal.details is DhUserDetails
        ) {
            val user = principal.details as DhUserDetails
            sessionsLock.write {
                val byUser = sessionsByUsers.get(user.id)
                if (byUser != null) {
                    byUser.remove(session)
                    if (byUser.isEmpty()) {
                        sessionsByUsers.remove(user.id)
                    }
                }
            }
        } else {
            session.close(CloseStatus.SESSION_NOT_RELIABLE)
        }
    }

    override fun handleTextMessage(session: WebSocketSession, message: TextMessage) {
        if (logger.isTraceEnabled) {
            logger.trace("WebSocket message from session with id = {}: {}", session.id, message.payload)
        }
    }


    @EventListener(DoctorAppEvent::class)
    fun onEvent(evt: DoctorAppEvent) {
        try {
            val sessionFilter = createByUserSessionFilter(evt.targetUsersIds)
            val message = createMessage(evt)
            sessionsLock.read {
                sessionsByUsers
                        .asSequence()
                        .filter(sessionFilter)
                        .forEach {
                            it.value.forEach { session ->
                                try {
                                    session.sendMessage(message)
                                } catch (e: Throwable) {
                                    logger.trace("Error while sending message to doctor app: {}", e.message)
                                }
                            }
                        }
            }
        } catch (e: Exception) {
            logger.error(e.message, e);
        }
    }

    private fun createMessage(evt: DoctorAppEvent): TextMessage {
        return TextMessage(
                objectMapper.writeValueAsString(
                        mapOf(
                                "messageType" to evt.content.javaClass.simpleName,
                                "content" to evt.content
                        )
                )
        )
    }

    private fun createByUserSessionFilter(targetIds: Set<String>?): (Map.Entry<String, MutableList<WebSocketSession>>) -> Boolean {
        return if (targetIds == null) {
            { true }
        } else {
            { targetIds.contains(it.key) }
        }
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(DoctorAppWebSocketHandler::class.java)
    }
}