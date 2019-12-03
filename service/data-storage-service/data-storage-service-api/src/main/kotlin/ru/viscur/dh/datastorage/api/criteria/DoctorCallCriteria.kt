package ru.viscur.dh.datastorage.api.criteria

class DoctorCallCriteria(
        val doctorIdIn: Set<String>? = null,
        val callerIdIn: Set<String>? = null,
        val orderBy: List<CriteriaOrderBy>? = null
)