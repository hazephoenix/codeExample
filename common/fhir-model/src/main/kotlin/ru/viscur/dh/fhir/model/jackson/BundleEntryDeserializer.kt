package ru.viscur.dh.fhir.model.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.type.BundleEntry

/**
 * Created at 11.10.2019 17:01 by SherbakovaMA
 *
 * Deserializer разбора json в [BundleEntry]
 */
class BundleEntryDeserializer @JvmOverloads constructor(vc: Class<*>? = null) : StdDeserializer<BundleEntry>(vc) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): BundleEntry {
        val node = p.codec.readTree<JsonNode>(p)
        val resourceType = node["resource"]["resourceType"].asText()
        return BundleEntry(
                ObjectMapper()
                        .treeToValue(
                                node["resource"],
                                ResourceType.byId(resourceType).entityClass
                        )
        )
    }
}