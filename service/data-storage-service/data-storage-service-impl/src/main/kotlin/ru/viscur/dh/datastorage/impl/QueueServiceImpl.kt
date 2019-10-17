package ru.viscur.dh.datastorage.impl

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
class QueueServiceImpl(
        private val locationService: LocationService,
        private val patientService: PatientService
): QueueService {

    @PersistenceContext
    private lateinit var em: EntityManager

    override fun queueItemsOfOffice(officeId: String): List<QueueItem> {
        val query = em.createNativeQuery("""
            select r.resource
            from QueueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef
            order by r.resource ->> 'onum'
            """)
        query.setParameter("officeRef", "Location/$officeId")
        return query.fetchResourceList()
    }

    @Tx
    override fun deleteQueueItemsOfOffice(officeId: String) {
        val query = em.createNativeQuery("""
            delete
            from QueueItem r
            where r.resource -> 'location' ->> 'reference' = :officeRef""")
        query.setParameter("officeRef", "Location/$officeId")
    }

    override fun isPatientInOfficeQueue(patientId: String): String? {
        //находим QueueItem с patientId. берем Ref(location)
        val query = em.createNativeQuery("""
            select r.resource
            from QueueItem r
            where r.resource -> 'subject' ->> 'reference' = :patientRef
            """)
        query.setParameter("patientRef", "Location/$patientId")
        return query.fetchResource<QueueItem>()?.location?.id
    }

    override fun involvedOffices(): List<Location> {
        val query = em.createNativeQuery("""
            select r.resource
            from Location r
            where r.resource ->> 'status' in (:WAITING_PATIENT, :OBSERVATION)
            """)
        query.setParameter("WAITING_PATIENT", LocationStatus.WAITING_PATIENT)
        query.setParameter("OBSERVATION", LocationStatus.OBSERVATION)
        return query.fetchResourceList()
    }

    override fun involvedPatients(): List<Patient> {
        val query = em.createNativeQuery("""
            select r.resource
            from Patient r
            where r.resource -> 'extension' ->> 'queueStatus' != :READY
            """)
        query.setParameter("READY", PatientQueueStatus.READY)
        return query.fetchResourceList()
    }
}