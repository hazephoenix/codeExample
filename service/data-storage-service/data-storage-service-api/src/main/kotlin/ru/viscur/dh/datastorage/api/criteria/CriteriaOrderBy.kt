package ru.viscur.dh.datastorage.api.criteria

class CriteriaOrderBy(
        val attributeName: String,
        val desc: Boolean = false
) {

    companion object {
        fun asc(attributeName: String) = CriteriaOrderBy(attributeName, false)
        fun desc(attributeName: String) = CriteriaOrderBy(attributeName, true)
    }
}