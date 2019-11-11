package triton

import org.slf4j.LoggerFactory
import triton.protocol.TritonProtocol
import triton.protocol.command.*
import triton.protocol.command.mode.E1Cmd
import triton.protocol.command.mode.E3Cmd
import triton.protocol.command.mode.W1Cmd
import triton.protocol.command.mode.WxCmd
import triton.protocol.enums.ReturnCode
import triton.protocol.enums.ReturnCode.Companion.throwOnErrorCode
import triton.protocol.packet.*
import triton.protocol.send
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.SocketException
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.Condition
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock


typealias WaveData1Listener = (TritonMonitorDevice.ListenerArgs<WavePacketData1>) -> Unit
typealias WaveData3Listener = (TritonMonitorDevice.ListenerArgs<WavePacketData3>) -> Unit
typealias WaveDataXfListener = (TritonMonitorDevice.ListenerArgs<WavePacketDataXf>) -> Unit
typealias ValueDataListener = (TritonMonitorDevice.ListenerArgs<ValuePacketData>) -> Unit
typealias ValueDataXfListener = (TritonMonitorDevice.ListenerArgs<ValuePacketDataXf>) -> Unit

/**
 * Представляет собой устройство монитора
 */
class TritonMonitorDevice(
        /**
         * MAC адрес устройства.
         */
        val mac: UByteArray,

        /**
         * IP адрес устройства. Будет назначен устройству
         */
        val ipAddress: Inet4Address,

        /**
         * Broadcast адрес (на него отправляются команды устрйоству)
         */
        val broadcastIpAddress: Inet4Address
) {

    enum class Mode { E1, E3, W1, Wx }

    /**
     * Протокол
     */
    private val protocol = TritonProtocol()

    /**
     * UDP сокет
     */
    private var serverSocket: DatagramSocket? = null

    private val lock = ReentrantLock()
    private val changeModeCondition = lock.newCondition()
    private val startSendingDataCondition = lock.newCondition()
    private val stopSendingDataCondition = lock.newCondition()
    private val measurePressureCondition = lock.newCondition()
    private val toggleSoundCondition = lock.newCondition()
    private val readVersionCondition = lock.newCondition()
    private val connectionWaitingCondition = lock.newCondition()

    private val resultByConditionBinding = mutableMapOf<Condition, ReturnCodePacket>()

    private val wavePacketData1Listeners = mutableListOf<WaveData1Listener>()
    private val wavePacketData3Listeners = mutableListOf<WaveData3Listener>()
    private val wavePacketDataXfListeners = mutableListOf<WaveDataXfListener>()
    private val valuePacketDataListeners = mutableListOf<ValueDataListener>()
    private val valuePacketDataXfListeners = mutableListOf<ValueDataXfListener>()

    var connected: Boolean = false

    /**
     * На данный момент устройство подключено (условно)
     */
    val isConnected: Boolean
        get() = connected

    /**
     * Запущен (методом [start])?
     */
    val isRunning: Boolean
        get() = lock.withLock {
            return serverSocket != null
        }


    /**
     * Запуск обработки сообщений от устройства.
     *
     * Создает UDP сокет и "слушает" его (в отдельном потоке)
     */
    fun start() {
        lock.withLock {
            if (serverSocket != null) {
                return
            }
            serverSocket = DatagramSocket(SERVER_PORT)
            val thread = Thread { receiving() }
            thread.start()
        }
    }


    /**
     * Остановка обработки от устройства ("прибивает" сокет)
     */
    fun stop() {
        lock.withLock {
            serverSocket?.run {
                close()
                serverSocket = null
                connected = false
            }
        }
    }

    /**
     * Запрос на устройство: Начать отправку данных по волнам (ЭКГ, PPG и т.д. в зависимости от режима работы устройства)
     * @see [startSendingDataThrowError]
     * @see [changeMode]
     * @see [changeModeThrowError]
     */
    fun startSendingData(): ReturnCode =
            sendAndWaitBindReturnCode(StartSendingDataCmd(), startSendingDataCondition)

    /**
     * Тоже самое что и [startSendingData].
     *
     * В случае кода отличного от [ReturnCode.Ok], будет выброшено исключение
     * @see [startSendingData]
     */
    fun startSendingDataThrowError() =
            throwOnErrorCode({ "Request device to send waves data" }) { startSendingData() }

    /**
     * Запрос на устройство: Закончить отправку данных по волнам (ЭКГ, PPG и т.д. в зависимости от режима работы устройства)
     */
    fun stopSendingData(): ReturnCode =
            sendAndWaitBindReturnCode(StopSendingDataCmd(), stopSendingDataCondition)

    /**
     * Запрос на устройство: Запустить измерение давления
     */
    fun measurePressure(): ReturnCode =
            sendAndWaitBindReturnCode(MeasurePressureCmd(), measurePressureCondition)

    /**
     * Тоже самое что и [measurePressure].
     *
     * В случае кода отличного от [ReturnCode.Ok], будет выброшено исключение
     * @see [measurePressure]
     */
    fun measurePressureThrowError() = throwOnErrorCode({ "" }) { measurePressure() }

    /**
     * Запрос на устройство: Вкл./Откл. звук
     */
    fun toggleSound(on: Boolean): ReturnCode =
            sendAndWaitBindReturnCode(ToggleSoundCmd(on), toggleSoundCondition)

    /**
     * Прочитать модель устройства и  версию его ПО
     */
    fun readVersionInfo(): VersionPacketData =
            sendAndWaitResponse(ReadVersionInfoCmd(), readVersionCondition) {
                val res = resultByConditionBinding[readVersionCondition]
                        ?: throw Exception("Not found bind result")
                res as VersionPacketData
            }


    /**
     *  Запрос на устройство: Изменить режим работы устройства.
     *
     * @return код выполнения операции изменения режима работы устройства
     */
    fun changeMode(mode: Mode): ReturnCode {
        val cmd = when (mode) {
            Mode.E1 -> E1Cmd()
            Mode.E3 -> E3Cmd()
            Mode.W1 -> W1Cmd()
            Mode.Wx -> WxCmd()
        }
        return sendAndWaitBindReturnCode(cmd, changeModeCondition)
    }

    /**
     * Тоже самое что и [changeMode], в случае кода отличного от [ReturnCode.Ok],
     * будет выброшено исключение
     */
    fun changeModeThrowError(mode: Mode) =
            throwOnErrorCode({ "Change device mode to ${mode.name}" }) { changeMode(mode) }

    /**
     * Добавление обработчика данных формата [WavePacketData1] от монитора.
     * @param listener обработчик для добавления
     * @return ссылка на [listener]
     */
    fun addWaveData1Listener(listener: WaveData1Listener): WaveData1Listener {
        wavePacketData1Listeners.add(listener)
        return listener
    }

    /**
     * Удаление обработчика добавленного методом [addWaveData1Listener]
     */
    fun removeWaveData1Listener(listener: WaveData1Listener) {
        wavePacketData1Listeners.remove(listener)
    }

    /**
     * Добавление обработчика данных формата [WavePacketData3] от монитора.
     * @param listener обработчик для добавления
     * @return ссылка на [listener]
     */
    fun addWaveData3Listener(listener: WaveData3Listener): WaveData3Listener {
        wavePacketData3Listeners.add(listener)
        return listener
    }

    /**
     * Удаление обработчика добавленного методом [addWaveData3Listener]
     */
    fun removeWaveData3Listener(listener: WaveData3Listener) {
        wavePacketData3Listeners.remove(listener)
    }

    /**
     * Добавление обработчика данных формата [WavePacketDataXf] от монитора.
     * @param listener обработчик для добавления
     * @return ссылка на [listener]
     */
    fun addWaveDataXfListener(listener: WaveDataXfListener): WaveDataXfListener {
        wavePacketDataXfListeners.add(listener)
        return listener
    }

    /**
     * Удаление обработчика добавленного методом [addWaveDataXfListener]
     */
    fun removeWaveDataXfListener(listener: WaveDataXfListener) {
        wavePacketDataXfListeners.remove(listener)
    }

    /**
     * Добавление обработчика данных формата [ValuePacketData] от монитора.
     * @param listener обработчик для добавления
     * @return ссылка на [listener]
     */
    fun addValueDataListener(listener: ValueDataListener): ValueDataListener {
        valuePacketDataListeners.add(listener)
        return listener
    }

    /**
     * Удаление обработчика добавленного методом [addValueDataListener]
     */
    fun removeValueDataListener(listener: ValueDataListener) {
        valuePacketDataListeners.remove(listener)
    }

    /**
     * Добавление обработчика данных формата [ValuePacketDataXf] от монитора.
     * @param listener обработчик для добавления
     * @return ссылка на [listener]
     */
    fun addValueDataXfListener(listener: ValueDataXfListener): ValueDataXfListener {
        valuePacketDataXfListeners.add(listener)
        return listener
    }

    /**
     * Удаление обработчика добавленного методом [addValueDataXfListener]
     */
    fun removeValueDataXfListener(listener: ValueDataXfListener) {
        valuePacketDataXfListeners.remove(listener)
    }


    /**
     * Ожидание подключения устройства (подключение условное) в течении заданнного времени
     *
     * Если в течении указанного времени подключения не произошло, выбрасывает исключение
     */
    fun waitConnection(waitTimeout: Long, unit: TimeUnit) {
        if (connected) {
            return
        }
        lock.withLock {
            if (connected) {
                return
            }
            connectionWaitingCondition.await(waitTimeout, unit)
            if (!connected) {
                throw Exception("Monitor device connection error")
            }
        }
    }

    /**
     * Прием и последующая обработка
     * сообщений от устройства (запускается в отдельном потоке)
     */
    private fun receiving() {
        log.trace("Staring communication thread")
        val socket = serverSocket!!
        val packet = DatagramPacket(ByteArray(RECEIVE_BUF_SIZE), RECEIVE_BUF_SIZE)
        while (true) {
            try {
                log.trace("Wait for UDP packet ...")
                socket.receive(packet)
                log.trace("UDP Packet received")
                val address = packet.address as? Inet4Address ?: continue
                if (isSkipAddress(address)) {
                    log.trace("\tUDP Skipped")
                    continue
                }
                val packetData = protocol.decodePacket(packet)
                if (packetData is LinkRequest) {
                    log.trace("\tLink request received")
                    if (mac.contentEquals(packetData.mac) && isZeroAddress(address)) {
                        log.trace("\t\tResponse to link request")
                        socket.send(LinkCmd(packetData, ipAddress), broadcastIpAddress, CLIENT_PORT)
                    } else {
                        log.trace("\t\tSkip link request (device already has IP)")
                    }
                } else {
                    if (!connected) {
                        lock.withLock {
                            if (!connected) {
                                connected = true
                                connectionWaitingCondition.signalAll()
                            }

                        }
                    }
                    when (packetData) {
                        is StartSendingDataReturnCode -> startSendingDataCondition.bindResultAndSignal(packetData)
                        is StopSendingDataReturnCode -> stopSendingDataCondition.bindResultAndSignal(packetData)
                        is MeasurePressureReturnCode -> measurePressureCondition.bindResultAndSignal(packetData)
                        is ToggleSoundReturnCode -> toggleSoundCondition.bindResultAndSignal(packetData)
                        is VersionPacketData -> readVersionCondition.bindResultAndSignal(packetData)
                        is E1ReturnCode,
                        is E3ReturnCode,
                        is W1ReturnCode,
                        is WxReturnCode -> changeModeCondition.bindResultAndSignal(packetData as ReturnCodePacket)
                        is ValuePacketData -> onValuePacketDataReceived(packetData)
                        is ValuePacketDataXf -> onValuePacketDataReceivedXf(packetData)
                        is WavePacketData1 -> onWavePacketData1Received(packetData)
                        is WavePacketData3 -> onWavePacketData3Received(packetData)
                        is WavePacketDataXf -> onWavePacketDataXfReceived(packetData)
                        else -> log.error("Don't know how to process packet of type '${packetData.javaClass}'")
                    }
                }
            } catch (e: TritonProtocol.UnsupportedPacket) {
                log.trace(
                        "\tReceived unsupported UDP packet: {}",
                        String(packet.data.copyOfRange(0, packet.length), Charsets.US_ASCII)
                )
            } catch (e: SocketException) {
                break
            }
        }
    }


    private fun onWavePacketData1Received(packetData: WavePacketData1) {
        wavePacketData1Listeners.forEachSafe { it(ListenerArgs(packetData, it)) }
    }

    private fun onWavePacketData3Received(packetData: WavePacketData3) {
        wavePacketData3Listeners.forEachSafe { it(ListenerArgs(packetData, it)) }
    }

    private fun onWavePacketDataXfReceived(packetData: WavePacketDataXf) {
        wavePacketDataXfListeners.forEachSafe { it(ListenerArgs(packetData, it)) }
    }

    private fun onValuePacketDataReceived(packetData: ValuePacketData) {
        valuePacketDataListeners.forEachSafe { it(ListenerArgs(packetData, it)) }
    }

    private fun onValuePacketDataReceivedXf(packetData: ValuePacketDataXf) {
        valuePacketDataXfListeners.forEachSafe { it(ListenerArgs(packetData, it)) }
    }

    private fun Condition.bindResultAndSignal(packet: ReturnCodePacket) {
        lock.withLock {
            resultByConditionBinding[this] = packet
            this.signalAll()
        }
    }

    private fun sendAndWaitBindReturnCode(cmd: AbstractCmd, condition: Condition): ReturnCode {
        return sendAndWaitResponse(cmd, condition) {
            val res = resultByConditionBinding[condition]?.returnCode ?: throw Exception("Not found bind result")
            resultByConditionBinding.remove(condition)
            res
        }
    }

    private fun <TResult> sendAndWaitResponse(cmd: AbstractCmd, condition: Condition, resultGetter: () -> TResult): TResult {
        lock.withLock {
            serverSocket!!.send(cmd, ipAddress, CLIENT_PORT)
            if (condition.await(RESPONSE_WAIT_TIMEOUT, TimeUnit.SECONDS)) {
                return resultGetter()
            } else {
                throw Exception("Command response timeout exceeded.")
            }
        }
    }

    private fun isSkipAddress(ip: Inet4Address): Boolean {
        // Обрабатываем сообщения:
        //   * IP адрес устройства с которым работаем
        //   * 0.0.0.0 - на этот адрес устройство отправляет LINK запрос
        return !(ip == ipAddress || isZeroAddress(ip))
    }

    private fun isZeroAddress(ip: Inet4Address): Boolean {
        return ZERO_ADDR.contentEquals(ip.address)
    }

    private fun <T> List<T>.forEachSafe(block: (T) -> Unit) {
        var idx = this.size - 1
        while (idx >= 0) {
            block(this[idx])
            --idx
        }
    }

    companion object {
        val log = LoggerFactory.getLogger(TritonMonitorDevice::class.java)
        val ZERO_ADDR = byteArrayOf(0.toByte(), 0.toByte(), 0.toByte(), 0.toByte())
        const val SERVER_PORT = 63001
        const val CLIENT_PORT = 63002
        const val RECEIVE_BUF_SIZE = 2048
        const val RESPONSE_WAIT_TIMEOUT = 5L

    }

    class ListenerArgs<T>(
            val value: T,
            val currentListener: (ListenerArgs<T>) -> Unit
    )

}