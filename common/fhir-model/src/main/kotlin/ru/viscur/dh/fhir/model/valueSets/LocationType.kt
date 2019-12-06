package ru.viscur.dh.fhir.model.valueSets

/**
 * Коды типов кабинетов/мест в [ValueSetName.LOCATION_TYPE] (ValueSet/Location_types)
 * Используются для [ru.viscur.dh.fhir.model.entity.Location.type]
 */
enum class LocationType(val id: String) {
    /**
     * Смотровая
     */
    INSPECTION("Inspection"),
    /**
     * Кабинет диагностики/процедурный кабинет
     */
    DIAGNOSTIC("Diagnostic"),
    /**
     * Красная зона
     */
    RED_ZONE("RedZone"),
    /**
     * Желтая зона
     */
    YELLOW_ZONE("YellowZone"),
    /**
     * Зеленая зона
     */
    GREEN_ZONE("GreenZone")
}