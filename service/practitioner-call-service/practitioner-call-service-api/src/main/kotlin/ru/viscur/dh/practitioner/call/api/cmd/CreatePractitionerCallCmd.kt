package ru.viscur.dh.practitioner.call.api.cmd


import ru.viscur.dh.fhir.model.enums.Severity
import ru.viscur.dh.practitioner.call.model.CallGoal
import ru.viscur.dh.practitioner.call.model.CallableSpecializationCategory

/**
 * Команда на создание вызова
 *
 * @property callerId ID врача который вызвал
 * @property practitionerId ID врача которого вызываем
 * @property specializationCategory категория специализации которую вызываем (TODO а надо?)
 * @property goal цель вызова
 * @property locationId ID кабинета для вызова
 * @property patientSeverity степень тяжести
 * @property comment комметарий
 */
class CreatePractitionerCallCmd(
        val callerId: String,
        var practitionerId: String,
        var specializationCategory: CallableSpecializationCategory,
        var goal: CallGoal,
        var locationId: String,
        val patientSeverity: Severity,
        var comment: String
)