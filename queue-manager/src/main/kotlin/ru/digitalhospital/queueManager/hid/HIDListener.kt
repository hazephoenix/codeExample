package ru.digitalhospital.queueManager.hid

import org.hid4java.HidDevice
import org.hid4java.HidManager
import org.hid4java.HidServices
import org.hid4java.HidServicesListener
import org.hid4java.event.HidServicesEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import ru.digitalhospital.queueManager.events.hid.DeviceAttached
import ru.digitalhospital.queueManager.events.hid.DeviceDetached
import ru.digitalhospital.queueManager.events.hid.DeviceFailure
import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

@Component
class HIDListener(private val publisher: ApplicationEventPublisher) : HidServicesListener {

    private lateinit var hidServices: HidServices

    override fun hidDeviceDetached(event: HidServicesEvent) {
        publisher.publishEvent(DeviceDetached(event.hidDevice))
    }

    override fun hidDeviceAttached(event: HidServicesEvent) {
        publisher.publishEvent(DeviceAttached(event.hidDevice))
    }

    override fun hidFailure(event: HidServicesEvent) {
        publisher.publishEvent(DeviceFailure(event.hidDevice))
    }

    @PostConstruct
    fun postConstruct() {
        hidServices = HidManager.getHidServices()
        hidServices.addHidServicesListener(this)
        hidServices.start()
    }

    @PreDestroy
    fun preDestroy() {
        hidServices.shutdown()
    }

    /**
     * Получить устройство.
     * @param vendorId  VendorId устройства
     * @param productId ProductId устройства
     * @return  дескриптор устройства, либо null, если устройство не найдено или к нему нет доступа
     */
    fun getDevice(vendorId: Int, productId: Int): HidDevice? {
        val device = hidServices.getHidDevice(vendorId, productId, null) ?: return null
        return if(device.isOpen || device.open()) device else null
    }

}
