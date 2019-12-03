package ru.viscur.dh.datastorage.impl

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.api.PractitionerService
import ru.viscur.dh.datastorage.api.ResourceService
import ru.viscur.dh.datastorage.api.util.isInspectionQualification
import ru.viscur.dh.datastorage.impl.config.PERSISTENCE_UNIT_NAME
import ru.viscur.dh.fhir.model.entity.Practitioner
import ru.viscur.dh.fhir.model.enums.ResourceType
import ru.viscur.dh.fhir.model.utils.code
import ru.viscur.dh.fhir.model.utils.genId
import ru.viscur.dh.fhir.model.utils.qualificationCategory
import ru.viscur.dh.fhir.model.valueSets.ValueSetName
import javax.persistence.EntityManager
import javax.persistence.PersistenceContext

/**
 * Created at 23.10.2019 17:18 by SherbakovaMA
 */
@Service
class PractitionerServiceImpl(
        private val resourceService: ResourceService,
        private val conceptService: ConceptService
) : PractitionerService {

    @PersistenceContext(unitName = PERSISTENCE_UNIT_NAME)
    private lateinit var em: EntityManager

    override fun all(withBlocked: Boolean): List<Practitioner> {
        val whereClause = if (withBlocked) "" else "where (r.resource->'extension'->>'blocked')\\:\\:boolean = false"
        val query = em.createNativeQuery("""
            select r.resource
            from practitioner r
            $whereClause""")
        return query.fetchResourceList()
    }

    override fun create(practitioner: Practitioner): Practitioner = resourceService.create(practitioner.apply {
        id = genId()
        extension = this.extension.apply {
            qualificationCategory = qualificationCategory(practitioner)
        }
    })

    override fun update(practitioner: Practitioner): Practitioner = resourceService.update(ResourceType.Practitioner, practitioner.id) {
        identifier = practitioner.identifier
        name = practitioner.name
        gender = practitioner.gender
        qualification = practitioner.qualification
        extension = practitioner.extension.apply {
            qualificationCategory = qualificationCategory(practitioner)
        }
    }

    override fun byId(id: String): Practitioner = resourceService.byId(ResourceType.Practitioner, id)

    override fun byQualifications(codes: List<String>): List<Practitioner> {
        if (codes.isEmpty()) return listOf()
        val codesStr = codes.mapIndexed { index, code -> "(?${index + 1})" }.joinToString(", ")
        val q = em.createNativeQuery("""
            select resource from
            (select * from (values $codesStr) q (qual)) q
            join
            (
                select r.resource->'extension'->>'qualificationCategory' pr_qual, r.resource resource 
                from practitioner r
                where (r.resource->'extension'->>'blocked')\:\:boolean = false
            ) pr
            on q.qual = pr.pr_qual
        """.trimIndent())
        q.setParameters(codes)
        return q.fetchResourceList()
    }

    override fun updateBlocked(practitionerId: String, value: Boolean): Practitioner =
            resourceService.update(ResourceType.Practitioner, practitionerId) {
                extension.blocked = value
            }

    override fun updateOnWork(practitionerId: String, value: Boolean, officeId: String?): Practitioner {
        var officeIdIntr = officeId
        if (value) {
            //заступление на смену
            val practitioner = byId(practitionerId)
            //у врача, который производит осмотры кабинет не указывается
            if (practitioner.isInspectionQualification()) {
                officeIdIntr = null
            } else if (officeIdIntr == null) {
                throw Exception("error while updating onWork value for practitioner with id '$practitionerId':" +
                        "for with qualification category ${practitioner.qualificationCategory()} officeId must be defined")
            }
        } else {
            //уход со смены
            officeIdIntr = null
        }
        return resourceService.update(ResourceType.Practitioner, practitionerId) {
            extension.onWork = value
            extension.onWorkInOfficeId = officeIdIntr
        }
    }

    /**
     * Определение категории специальности
     * У мед. работника м б несколько специальностей, но мы определяем только одну категорию, учитывая приоритет категории.
     * Так, если есть специальности "невролог" и "узист", то категория будет Невролог, т к это дает приоритет на оказывание осмотров по отв.
     */
    private fun qualificationCategory(practitioner: Practitioner) =
            practitioner.qualification.map { conceptService.parent(conceptService.byCode(ValueSetName.PRACTITIONER_QUALIFICATIONS, it.code.code()))!! }
                    .minBy { -(it.priority ?: 0.0) }!!.code
}