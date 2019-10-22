package ru.viscur.dh.datastorage.impl

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import ru.viscur.dh.fhir.model.entity.Bundle
import ru.viscur.dh.fhir.model.entity.Concept
import ru.viscur.dh.fhir.model.type.BundleEntry

@EnableAutoConfiguration
class Test {

    companion object {
        private val log = LoggerFactory.getLogger(Test::class.java)
    }

    @Test
    @Order(1)
    fun `should create & update resource`() {
        val bundle = Bundle(entry = listOf(BundleEntry(resource = Concept(code = "test", system ="", display = ""))))
        assertNotNull(bundle.id)

        log.info("test completed")
    }
}