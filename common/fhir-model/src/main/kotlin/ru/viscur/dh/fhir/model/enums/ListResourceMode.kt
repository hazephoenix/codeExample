package ru.viscur.dh.fhir.model.enums

/**
 * Created at 10.10.2019 15:14 by SherbakovaMA
 *
 * Тип списка [ListResource][ru.viscur.dh.fhir.model.entity.ListResource]
 */
enum class ListResourceMode {
    /**
     * Основной
     */
    working,
    /**
     * "Снимок" на опр. момент времени, не надо считать его текущим
     */
    snapshot,
    /**
     * Изменения - что добавлено или удалено к какому-либо списку
     */
    changes
}