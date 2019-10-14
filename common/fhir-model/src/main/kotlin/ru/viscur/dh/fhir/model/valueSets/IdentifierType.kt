package ru.viscur.dh.fhir.model.valueSets

/**
 * Created at 04.10.2019 16:49 by SherbakovaMA
 *
 * Типы идентификаторов. Значения-коды используются в [Identifier.type][ru.viscur.dh.fhir.model.type.Identifier.type]
 */
enum class IdentifierType {
    /**
     * id ресурса
     */
    RESOURCE,
    /**
     * Номер обращения
     */
    CLAIM_NUMBER,
    /**
     * Пасспорт
     */
    PASSPORT,
    /**
     * СНИЛС
     */
    SNILS,
    /**
     * ЕНП
     */
    ENP,
    /**
     * Полис в электронном виде
     */
    DIGITAL_ASSURANCE,
    /**
     * Полис в бумажном виде
     */
    PAPER_ASSURANCE,
    /**
     * Идентификатор браслета
     */
    BRACELET,
    /**
     * Идентификатор RFID метки
     */
    RFID,
    /**
     * Номер кабиента
     */
    OFFICE_NUMBER
}