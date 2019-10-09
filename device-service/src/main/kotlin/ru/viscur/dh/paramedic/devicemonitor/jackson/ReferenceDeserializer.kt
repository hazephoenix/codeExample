package ru.viscur.dh.paramedic.devicemonitor.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import ru.viscur.dh.paramedic.devicemonitor.dto.Identifier
import ru.viscur.dh.paramedic.devicemonitor.dto.Reference
import ru.viscur.dh.paramedic.devicemonitor.dto.Resource

/**
 * Created at 02.10.2019 17:24 by TimochkinEA
 */
class ReferenceDeserializer : JsonDeserializer<Reference>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Reference {
        val node = p.codec.readTree<JsonNode>(p)
        val value = node["reference"]["value"].textValue()
        val (resourceType, id) = value.split("/")
        return Reference(object : Resource {
            override val identifier: Identifier = Identifier(id)
            override val resourceType: String = resourceType
        })
    }
}
