package ru.viscur.dh.queue.impl.repository

import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import ru.viscur.dh.queue.impl.persistence.model.QueueItemPE

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Репозиторий для элементов очереди в кабинеты
 */
@Repository
interface QueueItemRepository : CrudRepository<QueueItemPE, Long> {

    /**
     * Все записи с сортировкой по кабинету и onum
     */
    fun findAllByOrderByOfficeAscOnumAsc(): List<QueueItemPE>

    @Query(value = "SELECT * FROM queue_items where user_id = :userId and office_id = :officeId ", nativeQuery = true)
    fun findByOfficeIdAndUserId(officeId: Long, userId: Long): List<QueueItemPE>

    /**
     * Удалить все записи об очереди определенного кабинета
     */
    @Modifying
    @Transactional
    @Query("delete from QueueItemPE where office.id = :officeId")
    fun deleteAllByOfficeIs(officeId: Long)
}
