package ru.viscur.dh.queue.api.model


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
data class QueueItemDto(
        var id: Long = 0L,
        var user: User = User(),
        var office: Office = Office(),
        var estDuration: Int = 0,
        var onum: Int = 0
) {

    override fun toString(): String {
        return "QueueItem(id=$id, user=$user, office=$office, estDuration=$estDuration, onum=$onum)"
    }
}
