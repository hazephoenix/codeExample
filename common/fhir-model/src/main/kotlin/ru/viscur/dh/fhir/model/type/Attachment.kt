package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.AttachmentContentType
import java.util.*

/**
 * Created at 01.10.2019 13:27 by SherbakovaMA
 *
 * Вложение
 *
 * @param contentType тип содержимого
 * @param data данные
 * @param title отображаемые заголовок
 * @param creation дата создания
 */
class Attachment @JsonCreator constructor(
        @JsonProperty("contentType") val contentType: AttachmentContentType,
        @JsonProperty("data") val data: ByteArray, /*TODO взлетит? (был org.hibernate.engine.jdbc.BinaryStream) */
        @JsonProperty("title") val title: String,
        @JsonProperty("creation") val creation: Date
//todo также есть url, language, size, hash. можно включить
)