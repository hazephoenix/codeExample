package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.*
import ru.viscur.dh.datastorage.api.LocationService
import ru.viscur.dh.datastorage.api.PatientService
import ru.viscur.dh.datastorage.api.QueueService
import ru.viscur.dh.datastorage.impl.config.annotation.Tx
import ru.viscur.dh.fhir.model.entity.Location
import ru.viscur.dh.fhir.model.entity.Patient
import ru.viscur.dh.fhir.model.entity.QueueItem
import ru.viscur.dh.fhir.model.enums.LocationStatus
import ru.viscur.dh.fhir.model.enums.PatientQueueStatus
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 16.10.2019 12:16 by SherbakovaMA
 */
@Service
class QueueServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService
): QueueService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun queueItemsOfOffice(officeId: String): MutableList<QueueItem> {
        val query = em.createNativeQuery("""
            select r.resource
            from QueueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef
            order by r.resource ->> 'onum'
            """)
        query.setParameter("officeRef", "Location/$officeId")
        return query.fetchResourceList<QueueItem>().map { queueItem ->
            val patientId = queueItem.subject.id
            queueItem.apply {
                severity = patientService.severity(patientId!!)
                patientQueueStatus = patientService.byId(patientId).extension.queueStatus
            }
        }.toMutableList()
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
}