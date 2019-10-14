package ru.viscur.dh.fhir.model.enums

/**
 * Created at 04.10.2019 10:23 by SherbakovaMA
 *
 * Статус ["ответника" QuestionnaireResponse][ru.viscur.dh.fhir.model.entity.QuestionnaireResponse] на вопросник
 */
enum class QuestionnaireResponseStatus {
    in_progress,
    completed,
    amended,
    entered_in_error,
    stopped
}