package ru.viscur.dh.fhir.model.valueSets

/**
 * Created at 13.11.2019 8:59 by SherbakovaMA
 *
 * Типы локаций: зоны для осмотров ответственными врачами в зависимости от степени тяжести
 * Являются частью значений из [ValueSetName.LOCATION_TYPE]
 */
enum class ZoneForInspectionOfResp(val code: String) {
    RED_ZONE_TYPE("RedZone"),
    YELLOW_ZONE_TYPE("YellowZone"),
    GREEN_ZONE_TYPE("GreenZone")
}