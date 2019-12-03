package ru.viscur.dh.datastorage.api.model

/**
 * Соответствие между id метки и id сотрудника
 * @param tagId id метки
 * @param practitionerId id сотрудника
 */
data class Tag(
        var tagId: String,
        var practitionerId: String
)
