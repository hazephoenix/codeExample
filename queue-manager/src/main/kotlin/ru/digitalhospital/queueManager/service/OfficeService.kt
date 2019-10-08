package ru.digitalhospital.queueManager.service

import org.springframework.stereotype.Service
import ru.digitalhospital.queueManager.ageGroup
import ru.digitalhospital.queueManager.dto.OfficeStatus
import ru.digitalhospital.queueManager.entities.Office
import ru.digitalhospital.queueManager.entities.OfficeProcessHistory
import ru.digitalhospital.queueManager.entities.User
import ru.digitalhospital.queueManager.msToSeconds
import ru.digitalhospital.queueManager.now
import ru.digitalhospital.queueManager.repository.OfficeProcessHistoryRepository
import ru.digitalhospital.queueManager.repository.OfficeRepository


/**
 * Created at 04.09.2019 8:52 by SherbakovaMA
 *
 * Сервис для работы с кабинетами
 */
@Service
class OfficeService(
        private val officeRepository: OfficeRepository,
        private val officeProcessHistoryRepository: OfficeProcessHistoryRepository
) {
    /**
     * Изменение статуса кабинета [office] на [newStatus]
     * @param userOfPrevProcess пациент закончившегося процесса
     */
    fun changeStatus(office: Office, newStatus: OfficeStatus, userOfPrevProcess: User? = null) {
        val now = now()
        println("office ${office.id} status is changed to $newStatus")
        val officeProcessHistory = OfficeProcessHistory(
                surveyTypeId = office.surveyType.id,
                status = office.status,
                fireDate = office.updatedAt,
                duration = msToSeconds(now.time - office.updatedAt.time)
        )

        userOfPrevProcess?.run {
            officeProcessHistory.apply {
                userType = type
                userDiagnostic = diagnostic
                userAgeGroup = ageGroup(birthDate!!)
            }
        }
        officeProcessHistoryRepository.save(officeProcessHistory)
        office.status = newStatus
        office.updatedAt = now
        officeRepository.save(office)
    }
}