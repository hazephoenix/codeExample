package ru.viscur.dh.apps.rfidlocationdevice

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.validation.annotation.Validated

@ConfigurationProperties(prefix = "agent")
@Validated
class RfidLocationDevicePropertiesImpl : RfidLocationDeviceProperties {
    override var replayFile: String = "<uninitialized>"
}
