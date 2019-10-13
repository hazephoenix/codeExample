package ru.viscur.dh.queue.impl.service

import ru.viscur.dh.queue.api.OfficeService
import ru.viscur.dh.queue.api.model.Office
import ru.viscur.dh.queue.api.model.OfficeStatus
import ru.viscur.dh.queue.api.model.User

class OfficeServiceImpl: OfficeService {
    override fun changeStatus(office: Office, newStatus: OfficeStatus, userOfPrevProcess: User?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}