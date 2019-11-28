package ru.viscur.dh.integration.mis.api.dto

/**
 * Created at 12.11.2019 18:08 by SherbakovaMA
 *
 * Информация о нагрузке врача
 *
 * @param practitioner информация о специалисте
 * @param observationListSize количество проведенных услуг
 * @param workload нагрузка (у каждой услуги смотрится какой степени тяжести был пациент, переводится в вес нагрузки, суммируется)
 */
data class WorkloadItemDto(
        val practitioner: PractitionerDto,
        val observationListSize: Int,
        val workload: Int
)