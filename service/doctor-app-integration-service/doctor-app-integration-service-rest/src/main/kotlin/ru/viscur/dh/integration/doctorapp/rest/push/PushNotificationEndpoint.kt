package ru.viscur.dh.integration.doctorapp.rest.push

import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.stereotype.Component
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.integration.doctorapp.api.event.DoctorAppEvent
import ru.viscur.dh.integration.doctorapp.api.event.MessageNewEvent
import ru.viscur.dh.integration.doctorapp.api.model.ClinicalImpression
import ru.viscur.dh.integration.doctorapp.api.model.Message
import ru.viscur.dh.integration.doctorapp.rest.DoctorAppWebSocketHandler
import ru.viscur.dh.integration.doctorapp.rest.push.protocol.Protocol
import ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet.AuthRequestPacket
import ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet.AuthResponsePacket
import ru.viscur.dh.integration.doctorapp.rest.push.protocol.packet.PushMessagePacket
import ru.viscur.dh.integration.doctorapp.rest.security.MisAuthenticationProvider
import ru.viscur.dh.security.DhUserDetails
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.lang.Exception
import java.net.*
import java.util.*
import java.util.concurrent.locks.ReentrantReadWriteLock
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import kotlin.concurrent.read
import kotlin.concurrent.write

/**
 * TODO У Java есть NIO, там есть API для poll()/select() модели,
 *      с помощью него можно сделать более оптимальную реализацию
 *      (не деражить количество потоков, равное количеству клиентов),
 *      Но с ним надо разбираться ... Класическую модель thread per client реализовать
 *      быстрее
 */
@Component
class PushNotificationEndpoint(
        resourceService: ResourceService
) {

    private val authProvider = MisAuthenticationProvider(resourceService)

    private lateinit var socket: ServerSocket
    private lateinit var acceptThread: Thread
    private lateinit var generateThread: Thread

    private val protocol = Protocol()
    private var running = true


    @PostConstruct
    private fun init() {
        socket = ServerSocket(41414)
        acceptThread = Thread(this::accept)
        //generateThread = Thread(this::sendRandomPush)
        acceptThread.start()
        //generateThread.start()
    }

    private val sessionsByUsers = mutableMapOf<String, MutableList<Session>>()
    private val sessionsLock = ReentrantReadWriteLock()

    private fun accept() {
        while (running) {
            val clientSocket = socket.accept()
            val input = DataInputStream(clientSocket.getInputStream())
            try {
                val packet = protocol.readPacket(input)
                if (packet !is AuthRequestPacket) {
                    throw IllegalStateException("Expecting AuthResponsePacket, but got '${packet.type}'")
                }
                val out = DataOutputStream(clientSocket.getOutputStream())

                try {
                    val auth = authProvider.authenticate(
                            UsernamePasswordAuthenticationToken(packet.login, packet.password)
                    )
                    val details = auth?.details as DhUserDetails
                    newSession(details, clientSocket, out);
                    protocol.writePacket(out, AuthResponsePacket(AuthResponsePacket.Code.Ok))
                } catch (e: AuthenticationException) {
                    protocol.writePacket(out, AuthResponsePacket(AuthResponsePacket.Code.IncorrectUserOrPassword))
                }
                out.flush()
            } catch (e: Exception) {
                closeQuite(clientSocket)
            }
        }
    }

    private fun newSession(details: DhUserDetails, clientSocket: Socket, out: DataOutputStream) {
        sessionsLock.write {
            val session = Session(details, clientSocket, out)
            session.serviceThread =
                    Thread { clientThread(session) }
            sessionsByUsers
                    .getOrPut(details.id, { mutableListOf() })
                    .add(session)
            session
                    .serviceThread!!
                    .start()
        }
    }

    private fun clientThread(session: Session) {
        val input = DataInputStream(session.socket.getInputStream())
        while (true) {
            try {
                protocol.readPacket(input)
            } catch (e: Exception) {
                closeQuite(session.socket)
                if (e is SocketException) {
                    sessionsLock.write {
                        sessionsByUsers[session.user.id]
                                ?.remove(session)
                    }
                }

            }
        }
    }

    private fun closeQuite(clientSocket: Socket) {
        try {
            clientSocket.close()
        } catch (ignore: IOException) {
            /**/
        }
    }

    @PreDestroy
    private fun destroy() {
        running = false;
        socket.close()
    }

    @EventListener(DoctorAppEvent::class)
    fun onEvent(evt: DoctorAppEvent) {
        try {
            val sessionFilter = createByUserSessionFilter(evt.targetUsersIds)
            val message = PushMessagePacket(evt.content)
            sessionsLock.read {
                sessionsByUsers
                        .asSequence()
                        .filter(sessionFilter)
                        .forEach {
                            var idx = it.value.size - 1
                            while (idx != -1) {
                                val session = it.value[idx]
                                synchronized(session) {
                                    try {
                                        protocol.writePacket(session.out, message)
                                    } catch (e: Throwable) {
                                        if (e is SocketException) {
                                            sessionsLock.write {
                                                it.value.removeAt(idx)
                                            }
                                        }
                                        logger.trace("Error while sending message to doctor app: {}", e.message)
                                    }
                                }
                                --idx
                            }
                        }
            }
        } catch (e: Exception) {
            logger.error(e.message, e);
        }
    }

    private fun createByUserSessionFilter(targetUsersIds: Set<String>?): (Map.Entry<String, MutableList<Session>>) -> Boolean {
        return targetUsersIds
                ?.let {
                    { entry: Map.Entry<String, MutableList<Session>> -> targetUsersIds.contains(entry.key) }
                }
                ?: { _: Map.Entry<String, MutableList<Session>> -> true }
    }


    private class Session(
            val user: DhUserDetails,
            val socket: Socket,
            val out: DataOutputStream
    ) {
        var serviceThread: Thread? = null

    }

    companion object {
        private val logger = LoggerFactory.getLogger(PushNotificationEndpoint::class.java)
    }
}