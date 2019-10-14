package ru.viscur.dh.fhir.model.enums

/**
 * Created at 03.10.2019 11:01 by SherbakovaMA
 *
 * Тип интерпретации результата значения [Quantity]
 */
enum class QuantityComparator {
    /**
     * <
     */
    lt,
    /**
     * <=
     */
    le,
    /**
     * >
     */
    gt,
    /**
     * >=
     */
    ge
}