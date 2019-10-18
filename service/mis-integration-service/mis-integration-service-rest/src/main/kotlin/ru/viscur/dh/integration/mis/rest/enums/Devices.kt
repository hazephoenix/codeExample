package ru.viscur.dh.integration.mis.rest.enums

/**
 * Доступные измерительные приборы подсистемы "АРМ Фельдшер"
 *
 * @property scanner Сканер документов
 * @property pulseOximeter Пульсоксиметр (кислород в крови)
 * @property scales Весы (ростомер)
 * @property stadiometer Ростомер
 * @property tonometer Тонометр
 * @property electrocardiograph ЭКГ
 * @property pyrometer Пирометр
 * @property heartRateMonitor Пульсометр
 * @property spirograph Спирограф
 */
enum class Devices {
    scanner,
    pulseOximeter,
    scales,
    stadiometer,
    tonometer,
    electrocardiograph,
    pyrometer,
    heartRateMonitor,
    spirograph
}