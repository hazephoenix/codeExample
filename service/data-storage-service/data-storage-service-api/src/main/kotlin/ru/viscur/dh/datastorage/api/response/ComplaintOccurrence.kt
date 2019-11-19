package ru.viscur.dh.datastorage.api.response

/**
 * Число вхождений искомых жалоб в список жалоб диагноза МКБ-10
 *
 * @param diagnosisCode код МКБ-10
 * @param complaintCodeCount число вхождений кодов жалоб
 */
data class ComplaintOccurrence(
        val diagnosisCode: String,
        val complaintCodeCount: Int
)