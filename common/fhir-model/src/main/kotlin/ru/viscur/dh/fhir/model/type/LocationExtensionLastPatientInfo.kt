package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 15.10.2019 10:02 by SherbakovaMA
 *
 * Информация кабинета о последнем принятом пациенте: кто и в какой кабинет отправили в очередь.
 *  если последний пациент имеет статус [UserInQueueStatus.FINISHED][ru.viscur.dh.queue.api.model.UserInQueueStatus.FINISHED],
 *  то кабинет не прописывается
 *  Информация должна удаляться как только пациент вошел на обследование в указанный кабинет или вообще покинул очередь
 *  Должна обновляться после каждого осмотра пациента
 *
 *  @param subject пациент, ссылка на [Patient] [ru.viscur.dh.fhir.model.entity.Patient]
 *  @param nextOffice кабинет, ссылка на [Location] [ru.viscur.dh.fhir.model.entity.Location]
 */
class LocationExtensionLastPatientInfo @JsonCreator constructor(
        @JsonProperty("subject") var subject: Reference,
        @JsonProperty("location") var nextOffice: Reference? = null
)