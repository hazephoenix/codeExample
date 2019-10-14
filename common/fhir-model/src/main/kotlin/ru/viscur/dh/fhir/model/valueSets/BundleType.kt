package ru.viscur.dh.fhir.model.valueSets

/**
 * Тип пакета данных, используются ресурсом [ru.viscur.dh.fhir.model.entity.Bundle]
 */
enum class BundleType(val value: String) {
    /**
     * Набор ресурсов
     */
    BATCH("batch"),
    /**
     * Документ
     */
    DOCUMENT("document"),
    /**
     * Транзакция
     */
    TRANSACTION("transaction")
}