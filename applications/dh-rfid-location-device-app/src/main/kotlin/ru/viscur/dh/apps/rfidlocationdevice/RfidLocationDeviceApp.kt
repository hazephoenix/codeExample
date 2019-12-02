package ru.viscur.dh.apps.rfidlocationdevice

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import picocli.CommandLine

@SpringBootApplication
@EnableConfigurationProperties(RfidLocationDevicePropertiesImpl::class)
class RfidLocationDeviceApp(
        private val rfidLocationDeviceProperties: RfidLocationDeviceProperties,
        private val rfidLocationCommandService: RfidLocationCommandService
) : CommandLineRunner {

    override fun run(args: Array<String>) {
        val dhArgs = EventAgentCommandLine(rfidLocationCommandService)
        CommandLine(dhArgs).execute(*args)
    }

}

fun main(args: Array<String>) {
    runApplication<RfidLocationDeviceApp>(*args)
}
