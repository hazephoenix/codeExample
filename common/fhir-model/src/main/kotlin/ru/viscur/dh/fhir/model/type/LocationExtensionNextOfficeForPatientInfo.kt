package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.fhir.model.utils.now
import java.util.*

/**
 * Created at 15.10.2019 10:02 by SherbakovaMA
 *
 * Информация кабинета о последнем принятом пациенте: кто и в какой кабинет отправили в очередь.
 *  если последний пациент имеет статус [UserInQueueStatus.FINISHED][ru.viscur.dh.queue.api.model.UserInQueueStatus.FINISHED],
 *  то кабинет не прописывается
 *  Информация должна удаляться как только пациент вошел на обследование в указанный кабинет или вообще покинул очередь
 *  Должна обновляться после каждого осмотра пациента
 *
 *  @param fireDate дата создания записи
 *  @param subject пациент, ссылка на [Patient] [ru.viscur.dh.fhir.model.entity.Patient]
 *  @param severity степень тяжести пациента
 *  @param queueCode номер в очереди
 *  @param nextOffice кабинет, ссылка на [Location] [ru.viscur.dh.fhir.model.entity.Location]
 */
class LocationExtensionNextOfficeForPatientInfo @JsonCreator constructor(
        @JsonProperty("fireDate") var fireDate: Date = now(),
        @JsonProperty("subject") var subject: Reference,
        @JsonProperty("severity") var severity: Severity,
        @JsonProperty("queueCode") var queueCode: String,
        @JsonProperty("nextOffice") var nextOffice: Reference
) {
    override fun toString(): String {
        return "LocationExtensionNextOfficeForPatientInfo(fireDate=$fireDate, patientId=${subject.id}, severity=$severity, queueCode='$queueCode', nextOfficeId=${nextOffice.id})"
    }
}