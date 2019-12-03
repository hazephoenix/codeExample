package ru.viscur.dh.datastorage.api.request

class PagedCriteriaRequest<T>(
        val criteria: T,
        /**
         * Номер страницы (с 0)
         */
        page: Int,

        /**
         * Размер страницы
         */
        pageSize: Int?
) : PagedRequest(page, pageSize)

