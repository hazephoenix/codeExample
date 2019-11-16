package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.digitalhospital.dhdatastorage.dto.RequestBodyForResources
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.QueueService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.transaction.desc.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.QueueHistoryOfPatient
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.MILLISECONDS_IN_SECOND
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 16.10.2019 12:16 by SherbakovaMA
 */
@Service
class QueueServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService,
        private val resourceService: ResourceService
) : QueueService {

    @PersistenceContext(name = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun queueItemsOfOffice(officeId: String): MutableList<QueueItem> {
        val query = em.createNativeQuery("""
            select r.resource
            from QueueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef
            order by r.resource ->> 'onum'
            """)
        query.setParameter("officeRef", "Location/$officeId")
        return query.fetchResourceList<QueueItem>().fillExtraFields().toMutableList()
    }

    @Tx
    override fun deleteQueueItemsOfOffice(officeId: String) {
        val query = em.createNativeQuery("""
            delete
            from QueueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef""")
        query.setParameter("officeRef", "Location/$officeId")
        query.executeUpdate()
    }

    override fun isPatientInOfficeQueue(patientId: String): String? {
        //находим QueueItem с patientId. берем Ref(location)
        val query = em.createNativeQuery("""
            select r.resource
            from QueueItem r
            where r.resource -> 'subject' ->> 'reference' = :patientRef
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        return query.fetchResource<QueueItem>()?.location?.id
    }

    override fun involvedOffices(): List<Location> {
        val query = em.createNativeQuery("""
            select r.resource
            from Location r
            where r.resource ->> 'status' in (:WAITING_PATIENT, :OBSERVATION)
            """)
        query.setParameter("WAITING_PATIENT", LocationStatus.WAITING_PATIENT.toString())
        query.setParameter("OBSERVATION", LocationStatus.OBSERVATION.toString())
        return query.fetchResourceList()
    }

    override fun involvedPatients(): List<Patient> {
        val query = em.createNativeQuery("""
            select r.resource
            from Patient r
            where r.resource -> 'extension' ->> 'queueStatus' != :READY
            """)
        query.setParameter("READY", PatientQueueStatus.READY.toString())
        return query.fetchResourceList()
    }

    override fun queueItems(): List<QueueItem> =
            resourceService.all(ResourceType.QueueItem, RequestBodyForResources(filter = mapOf())).fillExtraFields()

    fun List<QueueItem>.fillExtraFields() = this.map { queueItem ->
        val patientId = queueItem.subject.id
        queueItem.apply {
            severity = patientService.severity(patientId!!)
            patientQueueStatus = patientService.byId(patientId).extension.queueStatus
        }
    }

    override fun queueHistoryOfPatient(patientId: String, periodStart: Date, periodEnd: Date): List<QueueHistoryOfPatient> {
        val query = em.createNativeQuery("""
            select r.resource
            from QueueHistoryOfPatient r
            where r.resource -> 'subject' ->> 'reference' = :patientRef
                and (r.resource->>'fireDate')\:\:bigint / $MILLISECONDS_IN_SECOND >= :periodStart / $MILLISECONDS_IN_SECOND
                and (r.resource->>'fireDate')\:\:bigint / $MILLISECONDS_IN_SECOND <= :periodEnd / $MILLISECONDS_IN_SECOND
            order by r.resource ->> 'fireDate'
            """)
        query.setParameter("patientRef", "Patient/$patientId")
        query.setParameter("periodStart", periodStart.time)
        query.setParameter("periodEnd", periodEnd.time)
        return query.fetchResourceList()
    }

    override fun queueHistoryByPeriod(periodStart: Date, periodEnd: Date): List<QueueHistoryOfPatient> {
        //при сравнении переводим в секунды, чтобы в сравнении округление было до секунд
        val query = em.createNativeQuery("""
            select r.resource
            from QueueHistoryOfPatient r
            where r.resource->>'status' = '${PatientQueueStatus.IN_QUEUE}' 
                and (r.resource->>'fireDate')\:\:bigint / $MILLISECONDS_IN_SECOND <= :periodEnd / $MILLISECONDS_IN_SECOND
                and ((r.resource->>'fireDate')\:\:bigint + (r.resource->>'duration')\:\:bigint * $MILLISECONDS_IN_SECOND) / $MILLISECONDS_IN_SECOND >= :periodStart / $MILLISECONDS_IN_SECOND
            order by r.resource ->> 'fireDate'
            """)
        query.setParameter("periodStart", periodStart.time)
        query.setParameter("periodEnd", periodEnd.time)
        return query.fetchResourceList()
    }
}