package ru.viscur.dh.fhir.model.enums

/**
 * Created at 02.10.2019 12:55 by SherbakovaMA
 *
 * Тип пункта [QuestionnaireItem] вопросника [ru.viscur.dh.fhir.model.entity.Questionnaire]
 */
enum class QuestionnaireItemType {
    /**
     * Группа других пунктов
     */
    group,
    /**
     * Отображаемый текст (не требует ответа)
     */
    display,
    /**
     * Требуется ответ формата Булево
     */
    boolean,
    /**
     * Требуется ответ формата Вещественное число
     */
    decimal,
    /**
     * Требуется ответ формата Целое число
     */
    integer,
    /**
     * Требуется ответ формата Дата
     */
    date,
    /**
     * Требуется ответ формата Дата + время
     */
    dateTime,
    /**
     * Требуется ответ формата строка (нексолько слов, м б предложение)
     */
    string,
    /**
     * Требуется ответ формата строка (возможно до нескольких параграфов
     */
    text,
    /**
     * Требуется ответ формата ссылка
     */
    url,
    /**
     * Выбор кода из предложенных вариантов кодов
     */
    choice,
    /**
     * "Открытый выбор": выбор кода из предложенных вариантов кодов с возможностью указания другого кода в виде строки
     */
    open_choice,
    /**
     * Требуется ответ формата вложение
     */
    attachment,
    /**
     * Требуется ответ формата ссылка на ресурс ([ru.viscur.dh.fhir.model.type.Reference])
     */
    reference,
    /**
     * Требуется ответ формата [ru.viscur.dh.fhir.model.type.Quantity]
     */
    quantity,
    /**
     * Требуется ответ формата [ru.viscur.dh.fhir.model.type.SampledData]
     */
    sampled_data
}