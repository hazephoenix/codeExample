package ru.viscur.dh.practitioner.call.api.exception

import ru.viscur.dh.practitioner.call.model.CallStatus
import ru.viscur.dh.practitioner.call.model.PractitionerCall
import java.lang.RuntimeException

/**
 * Запрошенная операция не может быть выполнена для вызова врача, т.к.
 *  операция не может быть выполнена для вызовов в статусе, в котором находится вызов
 *  @property expectedStatus статус, который ожидает операция
 *  @property actualState актуальное состояние вызова
 */
class PractitionerCallWrongStatusException(
        val expectedStatus: CallStatus,
        val actualState: PractitionerCall
) : RuntimeException()