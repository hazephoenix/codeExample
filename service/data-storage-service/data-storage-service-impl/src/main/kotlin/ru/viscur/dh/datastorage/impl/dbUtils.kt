package ru.viscur.dh.datastorage.impl

import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.node.*
import ru.viscur.dh.fhir.model.dto.*
import ru.viscur.dh.fhir.model.entity.*
import ru.viscur.dh.fhir.model.enums.*
import ru.viscur.dh.fhir.model.type.*
import javax.persistence.*

private val dbResourceObjectMapper = ObjectMapper()
        .apply {
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

fun Query.patientsToExamine(): List<PatientToExamine> {
    return this.resultList
            .asSequence()
            .map {
                it as Array<*>
                PatientToExamine(
                        patient = it[2].toResourceEntity(),
                        patientId = it[1] as String,
                        carePlanStatus = CarePlanStatus.valueOf((it[3] as TextNode).asText()),
                        severity = dbResourceObjectMapper.treeToValue(it[0] as ObjectNode, Coding::class.java) as Coding
                )
            }
            .filterNotNull()
            .toList()
}
