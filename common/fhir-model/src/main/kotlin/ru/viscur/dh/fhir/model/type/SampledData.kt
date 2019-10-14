package ru.viscur.dh.fhir.model.type

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Created at 04.10.2019 8:41 by SherbakovaMA
 *
 * Дискретные значения измерений
 * Данные серии измерений, снятые с устройства, например с электрокардиографа
 *
 * первоначальное измеренное значение value(i) = [data] (i) * [factor] + [origin].value
 *
 * @param origin нулевое значение
 * @param period количество миллисекунд между значениями
 * @param factor умножить на это значение значения [data] перед добавлением к [origin]
 * @param lowerLimit нижний предел измерений
 * @param upperLimit верхний предел измерений
 * @param dimensions размерность - количество определяемых значений при однократном измерении (для конктретного времени)
 * @param data данные - измеренные значения: перечислены через пробел.
 *  Если имеется более одного измерения ([dimensions] > 1), то перечисления идут по времени:
 *  точки данных для конкретного времени представлены вместе (x1 y1 x2 y2...).
 *  Наряду с числовыми значениями возможны значения:
 *  - E - ошибка
 *  - L - ниже предела обнаружения
 *  - U - выше предела обнаружения
 */
class SampledData @JsonCreator constructor(
        @JsonProperty("origin") val origin: Quantity,
        @JsonProperty("period") val period: Double,
        @JsonProperty("factor") val factor: Double? = null,
        @JsonProperty("lowerLimit") val lowerLimit: Double? = null,
        @JsonProperty("upperLimit") val upperLimit: Double? = null,
        @JsonProperty("dimensions") val dimensions: Int = 1,
        @JsonProperty("data") val data: String
)