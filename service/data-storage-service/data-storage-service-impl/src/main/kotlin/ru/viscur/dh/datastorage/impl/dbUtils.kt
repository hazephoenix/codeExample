package ru.viscur.dh.datastorage.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import java.lang.Exception
import javax.persistence.Query

private fun objectMapper(): ObjectMapper {
    val mapper = ObjectMapper()
    // TODO в ответе есть атрибут meta, но в моделе у нас его нет
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    return mapper
}


fun <T> T?.toJsonb(): Any? where T : BaseResource {
    return objectMapper()
            .writeValueAsString(this)
}

fun jsonbParam(position: Int): String {
    return "?$position\\:\\:jsonb";
}

fun <T> Any?.toResourceEntity(): T?
        where T : BaseResource {
    return this?.let {
        it as ObjectNode
        val cls = ResourceType.byId(it["resourceType"].textValue()!!).entityClass
        return objectMapper().treeToValue(it, cls) as T
    }
}

fun <T> Query.fetchResource(): T?
        where T : BaseResource {
    return try {
        this.fetchResourceList<T>()
                .single()
    } catch (ex: NoSuchElementException) {
        null
    }
}

fun <T> Query.fetchResourceList(): List<T>
        where T : BaseResource {
    return this.resultList
            .asSequence()
            .map {
                (it as Array<Any?>)[0]
            }
            .filterNotNull()
            .map {
                it.toResourceEntity<T>()!!
            }.toList()
}
