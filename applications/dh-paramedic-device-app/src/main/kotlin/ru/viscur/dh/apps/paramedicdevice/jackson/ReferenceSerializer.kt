package ru.viscur.dh.apps.paramedicdevice.jackson

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import ru.viscur.dh.apps.paramedicdevice.dto.Reference

/**
 * Created at 02.10.2019 17:30 by TimochkinEA
 */
class ReferenceSerializer: JsonSerializer<Reference>() {
    override fun serialize(value: Reference, gen: JsonGenerator, serializers: SerializerProvider) {
        val view = "${value.value.resourceType}/${value.value.identifier.value}"
        gen.writeStartObject()
        gen.writeObjectFieldStart("reference")
        gen.writeStringField("value", view)
        gen.writeEndObject()
        gen.writeEndObject()
    }
}
