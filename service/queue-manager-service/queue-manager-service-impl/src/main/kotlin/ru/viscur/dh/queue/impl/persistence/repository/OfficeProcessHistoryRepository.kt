package ru.viscur.dh.queue.impl.repository

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.viscur.dh.queue.impl.persistence.model.OfficeProcessHistoryPE

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для истории работы кабинета (какой статус был когда и с какой продолжительностью)
 */
@Repository
interface OfficeProcessHistoryRepository : CrudRepository<OfficeProcessHistoryPE, Long> {

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_type = :userType and " +
            "user_diagnostic = :userDiagnostic and user_age_group = :userAgeGroup", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserTypeAndUserDiagnosticAndUserAgeGroup(
            surveyTypeId: Long, userType: String, userDiagnostic: String, userAgeGroup: Int
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_type = :userType and " +
            "user_diagnostic = :userDiagnostic", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserTypeAndUserDiagnostic(
            surveyTypeId: Long, userType: String, userDiagnostic: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_type = :userType and " +
            "user_age_group = :userAgeGroup", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserTypeAndUserAgeGroup(
            surveyTypeId: Long, userType: String, userAgeGroup: Int
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and " +
            "user_diagnostic = :userDiagnostic and user_age_group = :userAgeGroup", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserDiagnosticAndUserAgeGroup(
            surveyTypeId: Long, userDiagnostic: String, userAgeGroup: Int
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_type = :userType", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserType(
            surveyTypeId: Long, userType: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_diagnostic = :userDiagnostic", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserDiagnostic(
            surveyTypeId: Long, userDiagnostic: String
    ): Double?

    @Query(value = "SELECT avg(duration) FROM office_process_history where status = 'SURVEY' and " +
            "survey_type_id = :surveyTypeId and user_age_group = :userAgeGroup", nativeQuery = true)
    fun avgBySurveyTypeIdAndUserAgeGroup(
            surveyTypeId: Long, userAgeGroup: Int
    ): Double?
}
