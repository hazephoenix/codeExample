package ru.viscur.dh.fhir.model.enums

import ru.viscur.dh.fhir.model.valueSets.AutoCorrectionOfDefaultClinicalImpressionDuration.AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_RED
import ru.viscur.dh.fhir.model.valueSets.AutoCorrectionOfDefaultClinicalImpressionDuration.AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_YELLOW
import ru.viscur.dh.fhir.model.valueSets.AutoCorrectionOfDefaultClinicalImpressionDuration.AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_GREEN
import ru.viscur.dh.fhir.model.valueSets.ZoneForInspectionOfResp.RED_ZONE_TYPE
import ru.viscur.dh.fhir.model.valueSets.ZoneForInspectionOfResp.YELLOW_ZONE_TYPE
import ru.viscur.dh.fhir.model.valueSets.ZoneForInspectionOfResp.GREEN_ZONE_TYPE

/**
 * Created at 15.10.2019 8:47 by SherbakovaMA
 *
 * Степень тяжести пациента
 * Коды из system = 'ValueSet/Severity'
 *
 * @param display отображаемое название
 * @param zoneForInspectionOfResp зона для осмотров ответственными врачами
 * @param configAutoCorrectionDuration код настройки автокорректировки регламентного времени обслуживания
 * @param workloadWeight "вес" нагрузки
 */
enum class Severity(
        val display: String,
        val zoneForInspectionOfResp: String,
        val configAutoCorrectionDuration: String,
        val workloadWeight: Int
) {
    /**
     * Красный - тяжелая степень тяжести, реанимационный
     */
    RED("Красный", RED_ZONE_TYPE.code, AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_RED.code, 3),
    /**
     * Желтый - средней степени тяжести
     */
    YELLOW("Желтый", YELLOW_ZONE_TYPE.code, AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_YELLOW.code, 1),
    /**
     * Зеленый - удовлетворительное состояние
     */
    GREEN("Зеленый", GREEN_ZONE_TYPE.code, AUTO_CORRECTION_OF_DEFAULT_CLINICAL_IMPRESSION_DURATION_FOR_GREEN.code, 1)
}