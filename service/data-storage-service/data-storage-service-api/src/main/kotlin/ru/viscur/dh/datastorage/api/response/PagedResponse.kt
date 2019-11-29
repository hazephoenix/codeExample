package ru.viscur.dh.datastorage.api.response

class PagedResponse<T>(
        /**
         * Текущая страница (которую отдали)
         */
        val page: Int,

        /**
         * Сколько всего страниц есть
         */
        val pagesCount: Int,

        /**
         * Сколько всего записей есть
         */
        val totalItemsCount: Int,

        /**
         * Данные страницы
         */
        val data: List<T>
) {

    fun <TResult> map(mapper: (source: T) -> TResult) =
            PagedResponse(
                    page,
                    pagesCount,
                    totalItemsCount,
                    data.map(mapper)
            )

}