package ru.viscur.dh.datastorage.impl

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import javax.persistence.*

private val dbResourceObjectMapper = ObjectMapper()
        .apply {
            configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        }

fun <T> T?.toJson(): Any? where T : Any {
    return dbResourceObjectMapper
            .writeValueAsString(this)
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
        this.fetchResourceList<T>().firstOrNull()

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

fun Query.setParameters(params: List<Any>) {
    params.forEachIndexed { index, param ->
        this.setParameter(index + 1, param)
    }
}