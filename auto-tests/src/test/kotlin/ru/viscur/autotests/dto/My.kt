package ru.viscur.autotests.dto

import org.junit.jupiter.api.Test
import java.util.TreeMap

class My {

    @Test
    fun test() {
        val map: MutableMap<String, Any> = TreeMap()
        map["Omega"] = 24
        map["Alpha"] = 1
        map["Gamma"] = 3
        println(map)
    }

}