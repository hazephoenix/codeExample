package ru.viscur.dh.datastorage.api.request

open class PagedRequest(
        /**
         * Номер страницы (с 0)
         */
        val page: Int,

        /**
         * Размер страницы
         */
        val pageSize: Int?
) {
    fun <TCriteria> withCriteria(criteria: TCriteria): PagedCriteriaRequest<TCriteria> {
        return PagedCriteriaRequest<TCriteria>(criteria, page, pageSize)
    }
}