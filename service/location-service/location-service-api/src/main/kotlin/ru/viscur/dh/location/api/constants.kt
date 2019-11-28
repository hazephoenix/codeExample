package ru.viscur.dh.location.api

import java.time.Duration
import java.time.temporal.ChronoUnit


/**
 * Наименование очереди для получения сообщений artemis об местоположении
 * сотрудников
 */
const val LOCATION_EVENT_READER_CAUGHT_TAGS = "location/reader-caught-tags"

/**
 * Период времени с течении которого должна измениться позиция (зона) тега
 * для того чтобы считать его актуальным (сотрудник на работе)
 */
val LOCATION_ACTUAL_TAGS_DURATION = Duration.of(4, ChronoUnit.HOURS)!!
