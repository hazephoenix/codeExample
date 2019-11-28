package ru.viscur.dh.datastorage.impl.utils

import org.springframework.stereotype.Service
import ru.viscur.dh.datastorage.api.CodeMapService
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 21.11.2019 10:12 by SherbakovaMA
 *
 * Определяет специальности, которые м б назначены отв. пациенту
 */
@Service
class ResponsibleQualificationsPredictor(
        private val codeMapService: CodeMapService,
        private val conceptService: ConceptService
) {

    /**
     * По диагнозу + полу пациента + жалобам определяются специальности, которые м б назначены отв. такому пациенту
     */
    fun predict(diagnosis: String, gender: String, complaints: List<String>): List<String> {
        var qualifications = codeMapService.icdToPractitionerQualifications(diagnosis)
        //фильтруем по связанному полу. Для мужчин отсеивается гинеколог
        qualifications = qualifications.filter {
            val qualification = conceptService.byCode(ValueSetName.PRACTITIONER_QUALIFICATIONS, it.code)
            val relativeGender = qualification.relativeGender
            relativeGender.isNullOrEmpty() || relativeGender == gender
        }
        val complaintCodes = conceptService.byAlternativeOrDisplay(ValueSetName.COMPLAINTS, complaints)
        //случай, если у пациента есть жалобы, указанные в условиях назначения отв. специалиста (например, с сильной болью направляем к хирургу при I70-I79)
        val qualificationsFilteredByComplaints = qualifications.filter { qualification ->
            complaintCodes.any { complaintCode ->
                !qualification.condition.isNullOrEmpty() && complaintCode in qualification.condition!!.map { it.code }
            }
        }
        if (qualificationsFilteredByComplaints.isNotEmpty()) {
            return qualificationsFilteredByComplaints.map { it.code }
        }
        //случай, если у пациента нет сильной боли при I70-I79 (а хирург при сильной боли), тогда к терапевту, т к у него нет условий
        //или случай, если у специальности(ей) нет условий приема для этого диагноза
        val qualificationsWithoutConditions = qualifications.filter { it.condition.isNullOrEmpty() }
        if (qualificationsWithoutConditions.isNotEmpty()) {
            return qualificationsWithoutConditions.map { it.code }
        }
        //случай, если у всех специальностей для диагноза есть условия, которых нет у пациента
        // (A00-A09 - хирург при острой боли, терапевт при лихорадке, а у пациента нет таких жалоб - тогда все равнозначны и без фильтрации
        return qualifications.map { it.code }
    }
}