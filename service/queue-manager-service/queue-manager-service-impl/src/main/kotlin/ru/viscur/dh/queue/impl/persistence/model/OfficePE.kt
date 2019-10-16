package ru.viscur.dh.queue.impl.persistence.model


import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.queue.api.model.OfficeStatus
import ru.viscur.dh.queue.api.model.UserInQueueStatus
import ru.viscur.dh.queue.impl.SEVERITY_WITH_PRIORITY
import ru.viscur.dh.queue.impl.now
import ru.viscur.dh.queue.impl.repository.QueueItemRepository
import java.util.*
import javax.persistence.*

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
@Entity
@Table(name = "offices")
open class OfficePE(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "offices_seq")
        @SequenceGenerator(name = "offices_seq", sequenceName = "offices_seq", allocationSize = 0)
        var id: Long = 0L,
        @Column
        var name: String? = null,
        @ManyToOne(targetEntity = SurveyTypePE::class, cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "surveyTypeId", referencedColumnName = "id")
        var surveyType: SurveyTypePE = SurveyTypePE(),
        @Column
        @Enumerated(EnumType.STRING)
        var status: OfficeStatus = OfficeStatus.CLOSED,
        @Column
        var updatedAt: Date = now(),
        @Transient
        var queue: MutableList<QueueItemPE> = mutableListOf(),
        @Transient
        var lastUserInfo: Pair<UserPE, OfficePE?>? = null
) {
    /**
     * Предположительное время ожидания в очереди пациента с типом [type] =
     * Сумма приблизительных продолжительностей осмотра всех пациентов перед позицией в очереди, куда бы встал пациент с типом [type]
     */
    fun estWaitingInQueueWithType(type: Severity): Int {
        val inQueue = queue.filter { it.user.status != UserInQueueStatus.ON_SURVEY }
        val inQueueByType = when (type) {
            Severity.RED -> inQueue.filter { it.user.type == type }
            Severity.YELLOW -> inQueue.filter { it.user.type in SEVERITY_WITH_PRIORITY }
            else -> inQueue
        }
        return inQueueByType.sumBy { it.estDuration }
    }

    /**
     * Общее предположительное ожидание в очереди =
     * Сумма прибл. продолж. осмотра ВСЕХ пациентов в очереди [queue]
     */
    fun totalEstWaitingInQueue(): Int = queue.filter { it.user.status != UserInQueueStatus.ON_SURVEY }.sumBy { it.estDuration }

    /**
     * Добавить пациента в очередь
     */
    fun addUserToQueue(user: UserPE, estDuration: Int, queueItemRepository: QueueItemRepository) {
        val userType = user.type
        val queueItem = QueueItemPE(user = user, estDuration = estDuration)
        when (userType) {
            Severity.GREEN -> queue.add(queueItem)
            else -> {
                val userTypes = if (userType == Severity.RED) listOf(Severity.RED) else SEVERITY_WITH_PRIORITY
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
    }

    /**
     * Первый пациент в очереди
     */
    fun firstUserInQueue() = queue.firstOrNull()?.user

    /**
     * Удалить первого пациента из очереди
     */
    fun deleteFirstUserFromQueue(queueItemRepository: QueueItemRepository) {
        if (queue.isEmpty()) return
        queue.removeAt(0)
        updateQueueDataInDb(queueItemRepository)
    }

    /**
     * Удалить пациента из очереди
     */
    fun deleteUserFromQueue(user: UserPE, queueItemRepository: QueueItemRepository) {
        queue.removeAt(queue.indexOfFirst { it.user.id == user.id })
        updateQueueDataInDb(queueItemRepository)
    }

    /**
     * Удаление всех из очереди
     */
    fun deleteAllFromQueue(queueItemRepository: QueueItemRepository) {
        queue = mutableListOf()
        updateQueueDataInDb(queueItemRepository)
    }

    /**
     * Обновить данные по очереди в бд
     */
    open fun updateQueueDataInDb(queueItemRepository: QueueItemRepository) {
        queueItemRepository.deleteAllByOfficeIs(id)
        queue.forEachIndexed { i, queueItem ->
            queueItem.let {
                it.onum = i
                it.office = this
                it.id = 0L
            }
        }
        queueItemRepository.saveAll(queue)
    }

    override fun toString(): String {
        return "Office(id=$id, name=$name, type=$surveyType, status=$status, updatedAt=$updatedAt)"
    }
}
