package ru.viscur.dh.queue.api.model

import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.*


/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Кабинет
 *
 * @param id id кабинета
 * @param name наименование
 * @param surveyType тип обследования
 * @param status статус кабинета
 * @param updatedAt дата изменения статуса
 * @param queue очередь в кабинет
 * @param lastUserInfo информация о последнем принятом пациенте: кто и в какой кабинет отправили в очередь.
 *  если последний пациент имеет статус [UserInQueueStatus.FINISHED], то кабинет не прописывается
 *  Информация должна удаляться как только пациент вошел на обследование в указанный кабинет или вообще покинул очередь
 *  Должна обновляться после каждого осмотра пациента
 */
open class Office(
        var id: Long = 0L,
        var name: String? = null,
        var surveyType: SurveyType = SurveyType(),
        var status: OfficeStatus = OfficeStatus.CLOSED,
        var updatedAt: Date = Date.from(OffsetDateTime.now(ZoneOffset.UTC).toInstant()),
        @Transient
        var queue: MutableList<QueueItem> = mutableListOf(),
        @Transient
        var lastUserInfo: Pair<User, Office?>? = null
) {
    /**
     * Предположительное время ожидания в очереди пациента с типом [type] =
     * Сумма приблизительных продолжительностей осмотра всех пациентов перед позицией в очереди, куда бы встал пациент с типом [type]
     */
    /*fun estWaitingInQueueWithType(type: UserType): Int {
        val inQueue = queue.filter { it.user.status != UserInQueueStatus.ON_SURVEY }
        val inQueueByType = when (type) {
            UserType.RED -> inQueue.filter { it.user.type == type }
            UserType.YELLOW -> inQueue.filter { it.user.type in USER_TYPES_WITH_PRIORITY }
            else -> inQueue
        }
        return inQueueByType.sumBy { it.estDuration }
    }*/

    /**
     * Общее предположительное ожидание в очереди =
     * Сумма прибл. продолж. осмотра ВСЕХ пациентов в очереди [queue]
     */
    fun totalEstWaitingInQueue(): Int = queue.filter { it.user.status != UserInQueueStatus.ON_SURVEY }.sumBy { it.estDuration }

    /**
     * Добавить пациента в очередь
     */
   /* fun addUserToQueue(user: User, estDuration: Int, queueItemRepository: QueueItemRepository) {
        val userType = user.type
        val queueItem = QueueItem(user = user, estDuration = estDuration)
        when (userType) {
            UserType.GREEN -> queue.add(queueItem)
            else -> {
                val userTypes = if (userType == UserType.RED) listOf(UserType.RED) else USER_TYPES_WITH_PRIORITY
                if (queue.any { it.user.type in userTypes }) {
                    queue.add(queue.indexOfLast { it.user.type in userTypes } + 1, queueItem)
                } else {
                    if (queue.any { it.user.status == UserInQueueStatus.IN_QUEUE }) {
                        queue.add(queue.indexOfFirst { it.user.status == UserInQueueStatus.IN_QUEUE }, queueItem)
                    } else {
                        queue.add(queueItem)
                    }
                }
            }
        }
        updateQueueDataInDb(queueItemRepository)
    }*/

    /**
     * Первый пациент в очереди
     */
    fun firstUserInQueue() = queue.firstOrNull()?.user

    /**
     * Удалить первого пациента из очереди
     */
    /*fun deleteFirstUserFromQueue(queueItemRepository: QueueItemRepository) {
        if (queue.isEmpty()) return
        queue.removeAt(0)
        updateQueueDataInDb(queueItemRepository)
    }*/

    /**
     * Удалить пациента из очереди
     */
   /* fun deleteUserFromQueue(user: User, queueItemRepository: QueueItemRepository) {
        queue.removeAt(queue.indexOfFirst { it.user.id == user.id })
        updateQueueDataInDb(queueItemRepository)
    }
*/
    /**
     * Удаление всех из очереди
     */
   /* fun deleteAllFromQueue(queueItemRepository: QueueItemRepository) {
        queue = mutableListOf()
        updateQueueDataInDb(queueItemRepository)
    }*/

    /**
     * Обновить данные по очереди в бд
     */
  /*  open fun updateQueueDataInDb(queueItemRepository: QueueItemRepository) {
        queueItemRepository.deleteAllByOfficeIs(id)
        queue.forEachIndexed { i, queueItem ->
            queueItem.let {
                it.onum = i
                it.office = this
                it.id = 0L
            }
        }
        queueItemRepository.saveAll(queue)
    }*/

    override fun toString(): String {
        return "Office(id=$id, name=$name, type=$surveyType, status=$status, updatedAt=$updatedAt)"
    }
}
