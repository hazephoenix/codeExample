package ru.viscur.dh.apps.paramedicdevice.meddevice

import org.springframework.stereotype.Component
import ru.viscur.dh.apps.paramedicdevice.dto.Observation

/**
 * Created at 02.10.2019 15:12 by TimochkinEA
 */
@Component
class DumbMedDevice : IMedDevice {
    override fun take(): Observation {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
