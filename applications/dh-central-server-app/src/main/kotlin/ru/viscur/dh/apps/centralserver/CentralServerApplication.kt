package ru.viscur.dh.apps.centralserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CentralServerApplication

fun main(args: Array<String>) {
    runApplication<CentralServerApplication>(*args)
}