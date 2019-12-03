package ru.viscur.dh.datastorage.api.criteria

/**
 * Критерий поиска
 */
class DoctorMessageCriteria(
        /**
         * Ограничение по врачам
         */
        val doctorIdIn: List<String>? = null,

        /**
         * Типы сообщений
         */
        val type: Type = Type.Actual,

        /**
         * Сортировка
         */
        val orderBy: List<CriteriaOrderBy>? = null
) {

    enum class Type {
        /**
         * Только актуальные сообщения
         */
        Actual,

        /**
         * Только скрытые сообщения
         */
        Hidden,

        /**
         * Все сообщения не зависимо от того, скрытые они или нет
         */
        All
    }
}