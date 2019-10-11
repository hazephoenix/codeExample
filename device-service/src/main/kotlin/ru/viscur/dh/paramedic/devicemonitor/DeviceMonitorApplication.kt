package ru.viscur.dh.paramedic.devicemonitor

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeviceMonitorApplication

fun main(args: Array<String>) {
    runApplication<DeviceMonitorApplication>(*args)
}
