package ru.viscur.dh.apps.paramedicdevice.bean

import org.springframework.beans.factory.DisposableBean
import org.springframework.beans.factory.FactoryBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import triton.TritonMonitorDevice
import java.lang.Exception
import java.net.Inet4Address
import java.net.InetAddress

@Component
@Profile("triton-monitor")
class TritonMonitorFactoryBean(
        @Value("\${paramedic.triton.ip}") val ipAddress: String,
        @Value("\${paramedic.triton.broadcast}") val broadcastAddress: String,
        @Value("\${paramedic.triton.mac}") val macAddress: String
) : FactoryBean<TritonMonitorDevice>, DisposableBean {

    val instance = TritonMonitorDevice(
            parseMacAddress(macAddress),
            InetAddress.getByName(ipAddress) as Inet4Address,
            InetAddress.getByName(broadcastAddress) as Inet4Address
    )

    override fun getObjectType() = TritonMonitorDevice::class.java

    override fun getObject(): TritonMonitorDevice? {
        if (!instance.isRunning) {
            instance.start()
        }
        return instance
    }

    private fun parseMacAddress(macAddress: String): UByteArray {
        try {
            val mac = UByteArray(6)
            val address = macAddress.split(":")
            if (address.size != 6) {
                throw Exception("Wrong count of address segments")
            }
            address.forEachIndexed { index, it ->
                mac[index] = it.toUByte(16)
            }
            return mac
        } catch (e: Exception) {
            throw Exception("Bad parameter 'paramedic.triton.mac': ${e.message}", e)
        }
    }

    override fun destroy() {
        instance.stop()
    }


}