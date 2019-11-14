package ru.viscur.dh.integration.mis.rest

import org.springframework.web.bind.annotation.*
import ru.viscur.dh.integration.mis.api.ReportService
import ru.viscur.dh.integration.mis.api.dto.PeriodRequestBody

/**
 * Created at 12.11.2019 17:22 by SherbakovaMA
 *
 * Контроллер для отчетов
 */
@RestController
@RequestMapping("/report")
class ReportController(
        private val reportService: ReportService
) {

    /**
     * see [ReportService.queueHistoryOfPatient]
     */
    @GetMapping("/queueHistoryOfPatient")
    fun queueHistoryOfPatient(@RequestParam patientId: String) = reportService.queueHistoryOfPatient(patientId)

    /**
     * see [ReportService.observationHistoryOfPatient]
     */
    @GetMapping("/observationHistoryOfPatient")
    fun observationHistoryOfPatient(@RequestParam patientId: String) = reportService.observationHistoryOfPatient(patientId)

    /**
     * Информация об очередях на тек момент
     * В зависимости от параметров: для кабинета, для мед. персонала или вся
     * see [ReportService.queueInOffice]
     * see [ReportService.queueOfPractitioner]
     * see [ReportService.queueInOffices]
     */
    @GetMapping("/queue")
    fun queue(
            @RequestParam(required = false) officeId: String? = null,
            @RequestParam(required = false) practitionerId: String? = null
    ) = when {
        officeId != null -> reportService.queueInOffice(officeId)
        practitionerId != null -> reportService.queueOfPractitioner(practitionerId)
        else -> reportService.queueInOffices()
    }

    /**
     * see [ReportService.workload]
     */
    @GetMapping("/workload")
    fun workload() = reportService.workload()

    /**
     * see [ReportService.workloadHistory]
     */
    @GetMapping("/workloadHistory")
    fun workloadHistory(@RequestBody period: PeriodRequestBody) = reportService.workloadHistory(period.start, period.end)


    /**
     * see [ReportService.queueHistory]
     */
    @GetMapping("/queueHistory")
    fun queueHistory(@RequestBody period: PeriodRequestBody) = reportService.queueHistory(period.start, period.end)


}