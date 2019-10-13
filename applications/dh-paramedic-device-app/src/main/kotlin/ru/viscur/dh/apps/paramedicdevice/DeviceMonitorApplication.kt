package ru.viscur.dh.apps.paramedicdevice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DeviceMonitorApplication

fun main(args: Array<String>) {
    runApplication<DeviceMonitorApplication>(*args)
}
