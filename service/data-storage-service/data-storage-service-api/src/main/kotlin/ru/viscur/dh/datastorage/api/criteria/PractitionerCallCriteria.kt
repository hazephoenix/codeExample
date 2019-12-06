package ru.viscur.dh.datastorage.api.criteria

class PractitionerCallCriteria(
        val practitionerIdIn: Set<String>? = null,
        val callerIdIn: Set<String>? = null,
        val orderBy: List<CriteriaOrderBy>? = null
)