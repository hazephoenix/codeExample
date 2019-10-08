package ru.digitalhospital.queueManager.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.digitalhospital.queueManager.entities.RouteSheetItem

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для элементов маршрутных листов пациентов
 */
@Repository
interface RouteSheetItemRepository : CrudRepository<RouteSheetItem, Long> {

    /**
     * Все записи с сортировкой по пациенту и onum
     */
    fun findAllByOrderByUserAscOnumAsc(): List<RouteSheetItem>

    @Query(value = "SELECT * FROM route_sheet_items where user_id = :userId and survey_type_id = :surveyTypeId ", nativeQuery = true)
    fun findBySurveyTypeIdAndUserId(surveyTypeId: Long, userId: Long): List<RouteSheetItem>

    /**
     * Удалить все записи маршрутного листа определенного пациента
     */
    @Modifying
    @Transactional
    @Query("delete from RouteSheetItem where user.id = :userId")
    fun deleteAllByUserIs(userId: Long)
}
