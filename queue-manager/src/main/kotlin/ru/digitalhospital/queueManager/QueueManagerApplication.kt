package ru.digitalhospital.queueManager

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class QueueManagerApplication

fun main(args: Array<String>) {
    runApplication<QueueManagerApplication>(*args)
}
