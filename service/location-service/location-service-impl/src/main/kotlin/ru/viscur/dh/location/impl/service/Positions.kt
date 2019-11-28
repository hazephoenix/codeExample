package ru.viscur.dh.location.impl.service

import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component
import ru.viscur.dh.location.api.LOCATION_ACTUAL_TAGS_DURATION
import java.time.Instant

/**
 * Компонент сохраняет текущее местоположение сотрудников
 * (practitionerId to zoneId)
 */
@Component
@Scope("singleton")
class Positions(private val map: MutableMap<String, String>) : MutableMap<String, String> by map {

    private val mem: MutableMap<String, Instant> = HashMap()

    /**
     * @return список тегов которые меняли положение в период [LOCATION_ACTUAL_TAGS_DURATION]
     */
    fun getActual(): Set<String> {
        val lim = Instant.now() - LOCATION_ACTUAL_TAGS_DURATION
        return mem.filter { (_, value) -> value.isAfter(lim) }.keys
    }

    override fun clear() {
        map.clear()
        mem.clear()
    }

    override fun put(key: String, value: String): String? = map.put(key, value)?.also { oldValue ->
        if (oldValue != value) {
            mem[key] = Instant.now()
        }
    }

    override fun putAll(from: Map<out String, String>) {
        val now = Instant.now()
        from.forEach { (key, value) ->
            map[key] = value;
            mem[key] = now
        }
    }
}
