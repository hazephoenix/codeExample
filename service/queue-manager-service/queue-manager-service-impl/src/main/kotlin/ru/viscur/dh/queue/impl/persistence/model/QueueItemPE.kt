package ru.viscur.dh.queue.impl.persistence.model

import javax.persistence.*

/**
 * Created at 03.09.19 12:36 by SherbakovaMA
 *
 * Элемент в очередях в кабинеты (описание пациента в очереди в кабинет)
 *
 * @param id id записи в базе
 * @param user пациент
 * @param office кабинет
 * @param estDuration предположительная продолжительность осмотра
 * @param onum порядковый номер в очередь (в определенный кабинет)
 */
@Entity
@Table(name = "queue_items")
data class QueueItemPE(
        @Id
        @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "queue_items_seq")
        @SequenceGenerator(name = "queue_items_seq", sequenceName = "queue_items_seq", allocationSize = 0)
        var id: Long = 0L,
        @ManyToOne(targetEntity = UserPE::class, cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "userId", referencedColumnName = "id")
        var user: UserPE = UserPE(),
        @ManyToOne(targetEntity = OfficePE::class, cascade = [CascadeType.MERGE], fetch = FetchType.EAGER)
        @JoinColumn(name = "officeId", referencedColumnName = "id")
        var office: OfficePE = OfficePE(),
        @Column
        var estDuration: Int = 0,
        @Column
        var onum: Int = 0
) {

    override fun toString(): String {
        return "QueueItem(id=$id, user=$user, office=$office, estDuration=$estDuration, onum=$onum)"
    }
}
