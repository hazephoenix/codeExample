package ru.digitalhospital.queueManager.hid.rfid

import org.hid4java.HidDevice
import org.springframework.stereotype.Component
import ru.digitalhospital.queueManager.hid.CMD_ON
import ru.digitalhospital.queueManager.hid.COMMAND_REPORT_ID
import ru.digitalhospital.queueManager.hid.HIDListener
import ru.digitalhospital.queueManager.hid.VENDOR_ID
import javax.annotation.PostConstruct

/**
 * RFID Reader
 */
@Component
class Reader(private val hidListener: HIDListener) {

    companion object {
        private const val PROD_ID = 0x5750
    }

    private var reader: HidDevice? = null

    @PostConstruct
    fun postConstruct() {
        reader = hidListener.getDevice(VENDOR_ID, PROD_ID)
        reader?.write(byteArrayOf(COMMAND_REPORT_ID, CMD_ON), 2, 0x0)
    }
}
