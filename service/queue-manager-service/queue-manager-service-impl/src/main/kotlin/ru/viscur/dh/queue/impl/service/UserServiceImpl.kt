package ru.viscur.dh.queue.impl.service

import org.springframework.stereotype.Service
import ru.viscur.dh.queue.api.UserService
import ru.viscur.dh.queue.api.model.User
import ru.viscur.dh.queue.api.model.UserInQueueStatus
import java.util.*

@Service
class UserServiceImpl: UserService {

    override fun changeStatus(user: User, newStatus: UserInQueueStatus, officeIdOfPrevProcess: Long?, saveCurrentStatusToHistory: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun saveCurrentStatus(user: User, officeIdOfPrevProcess: Long?, now: Date) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }


}
