package ru.digitalhospital.queueManager

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import ru.digitalhospital.queueManager.dto.OfficeStatus
import ru.digitalhospital.queueManager.dto.UserInQueueStatus
import ru.digitalhospital.queueManager.dto.UserType
import ru.digitalhospital.queueManager.entities.Office
import ru.digitalhospital.queueManager.entities.SurveyType
import ru.digitalhospital.queueManager.entities.User
import ru.digitalhospital.queueManager.repository.UserRepository
import ru.digitalhospital.queueManager.service.QueueManager
import java.text.SimpleDateFormat

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [QueueManagerApplication::class])
class RegisterUserTest {

    @Autowired
    private lateinit var queueManager: QueueManager
    @Autowired
    private lateinit var userRepository: UserRepository

    companion object {
        private val users = listOf(
                User(firstName = "u1", lastName = "1", type = UserType.GREEN, birthDate = date("1.09.2000 20:00"), diagnostic = "d1"),
                User(firstName = "u2", lastName = "2", type = UserType.RED, birthDate = date("1.09.80 20:00"), diagnostic = "d2"),
                User(firstName = "u3", lastName = "3", type = UserType.YELLOW, birthDate = date("1.09.2010 20:00"), diagnostic = "d3")
        )

        private fun date(timeStr: String) = SimpleDateFormat("dd.MM.yyyy hh:mm").parse(timeStr)
    }

    @Test
    fun testAddingToQueue() {
        queueManager.surveyTypes = listOf(SurveyType(1, "blood"))
        val office = Office(1, "blood-office", queueManager.surveyTypes[0])
        queueManager.offices = listOf(office)
        val actOffice = queueManager.offices.first()
        Assert.assertEquals("offices size", 1, queueManager.offices.size)
        Assert.assertEquals("office.id", office.id, actOffice.id)
        Assert.assertEquals("office.name", office.name, actOffice.name)
        Assert.assertEquals("office.default status", OfficeStatus.CLOSED, office.status)

        queueManager.registerUser(users[0], listOf(Pair(1L, 0.0)))
        Assert.assertEquals("queue size", 1, actOffice.queue.size)
        compareUsers(users[0], actOffice.firstUserInQueue()!!, "first green type user")

        queueManager.registerUser(users[1], listOf(Pair(1L, 0.0)))
        Assert.assertEquals("queue size", 2, actOffice.queue.size)
        compareUsers(users[1], actOffice.queue[0].user, "first red type user")
        compareUsers(users[0], actOffice.queue[1].user, "second green type user")

        queueManager.registerUser(users[2], listOf(Pair(1L, 0.0)))
        Assert.assertEquals("queue size", 3, actOffice.queue.size)
        compareUsers(users[1], actOffice.queue[0].user, "first red type user")
        compareUsers(users[2], actOffice.queue[1].user, "second yellow type user")
        compareUsers(users[0], actOffice.queue[2].user, "third green type user")

        testLeftQueue()
    }

    fun testLeftQueue() {
        users.forEach {
            queueManager.userLeftQueue(it)
        }
        users.forEach {
            Assert.assertFalse("user with id ${it.id} must be deleted in db", userRepository.findById(it.id).isPresent)
        }

    }

    private fun compareUsers(expUser: User, actUser: User, comment: String) {
        Assert.assertEquals("$comment: user.id", expUser.id, actUser.id)
        Assert.assertEquals("$comment: user.firstName", expUser.firstName, actUser.firstName)
        Assert.assertEquals("$comment: user status in queue", UserInQueueStatus.IN_QUEUE, actUser.status)
    }

}