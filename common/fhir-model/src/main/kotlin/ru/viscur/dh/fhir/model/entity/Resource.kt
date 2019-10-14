package ru.viscur.dh.fhir.model.entity

import com.fasterxml.jackson.annotation.JsonCreator
/*import com.vladmihalcea.hibernate.type.json.JsonBinaryType
import com.vladmihalcea.hibernate.type.json.JsonStringType
import org.hibernate.annotations.Type
import org.hibernate.annotations.TypeDef
import org.hibernate.annotations.TypeDefs*/
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.genId
import java.util.*
/*
import javax.persistence.Column
import javax.persistence.Id
*/

/**
 * Created at 01.10.2019 12:58 by SherbakovaMA
 *
 * Описание ресурса
 *
 * @param id id ресурса (строковый)
 * @param txid id ресурса (числовой)
 * @param ts todo
 * @param resourceType тип ресурса. Дубль значения в resource.resourceType
 * @param status статус: 'created', 'updated', 'deleted', 'recreated' todo есть он?
 * @param resource данные
 */
/*
        TODO зачем этот класс?
@TypeDefs(
        TypeDef(name = "json", typeClass = JsonStringType::class),
        TypeDef(name = "jsonb", typeClass = JsonBinaryType::class)
)*/
class Resource @JsonCreator constructor(
//        @Id
        val id: String = genId(),
//        @Column
        val txid: Long,
//        @Column
        val ts: Date,
//        @Column
        val resourceType: ResourceType,
//        @Type(type = "jsonb")
//        @Column(columnDefinition = "jsonb")
        val resource: BaseResource
) {
    constructor(id: String = genId(), txid: Long, ts: Date, baseResource: BaseResource) : this(id, txid, ts, baseResource.resourceType, baseResource)
}