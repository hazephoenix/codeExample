package ru.viscur.dh.datastorage.impl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import ru.viscur.dh.fhir.model.entity.BaseResource
import ru.viscur.dh.fhir.model.enums.ResourceType
import javax.persistence.Query

private val dbResourceObjectMapper = ObjectMapper()
        .apply {
            // TODO в ответе есть атрибут meta, но в моделе у нас его нет
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

fun <T> T?.toJsonb(): Any? where T : BaseResource {
    return dbResourceObjectMapper
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
        return dbResourceObjectMapper.treeToValue(it, cls) as T
    }
}

fun <T> Query.fetchResource(): T?
        where T : BaseResource =
        this.fetchResourceList<T>().singleOrNull()

fun <T> Query.fetchResourceList(): List<T>
        where T : BaseResource {
    return this.resultList
            .asSequence()
            .map {
                if(it is Array<*>) {
                    it[0]
                } else {//если запрос идет на одно поле resource, то это не Array
                    it
                }
            }
            .filterNotNull()
            .map {
                it.toResourceEntity<T>()!!
            }.toList()
}
