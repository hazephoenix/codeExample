package ru.viscur.dh.queue.api.cmd

import ru.viscur.dh.queue.api.model.User

/**
 * Created at 11.09.2019 9:42 by SherbakovaMA
 *
 * Информация для регистрации пациента в очереди
 *
 * @param user пациент
 * @param surveys обследования которые нужно пройти: id обследования и приоритет
 */
class RegisterUserCMD(val user: User, val surveys: List<Survey>) {

    /**
     * Обследование назначенное пациенту
     */
    data class Survey (
            /**
             * ID типа обследования
             *
             * @see [ru.viscur.dh.queue.api.model.SurveyType.id]
             */
            val surveyTypeId: Long,

            /**
             * Приоритет обследования с типом [surveyTypeId] в маршрутном листе пациента
             */
            val priority: Double
    )

}
