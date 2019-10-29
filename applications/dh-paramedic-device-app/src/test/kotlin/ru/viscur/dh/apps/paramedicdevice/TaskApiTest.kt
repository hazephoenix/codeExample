package ru.viscur.dh.apps.paramedicdevice

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.mock.mockito.SpyBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.api.TaskController
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.dto.TaskStatus
import ru.viscur.dh.apps.paramedicdevice.dto.TaskType
import ru.viscur.dh.apps.paramedicdevice.dto.TvesResponse
import ru.viscur.dh.apps.paramedicdevice.device.Height
import ru.viscur.dh.apps.paramedicdevice.device.Weight
import ru.viscur.dh.apps.paramedicdevice.service.TaskDispatcher
import java.util.regex.Pattern

/**
 * Created at 28.10.2019 14:39 by TimochkinEA
 */
@RunWith(SpringRunner::class)
@WebMvcTest(TaskController::class)
class TaskApiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @SpyBean
    private lateinit var dispatcher: TaskDispatcher

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @SpyBean
    private lateinit var height: Height

    @SpyBean
    private lateinit var weight: Weight

    @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
    private lateinit var tvesUrl: String


    @Before
    fun beforeClass() {
        Mockito.`when`(
                restTemplate.getForEntity("${tvesUrl}/height", TvesResponse::class.java)
        ).thenReturn(
                ResponseEntity(
                        TvesResponse(value = "179", unit = "см.", code = "OK", message = "OK"),
                        HttpStatus.OK
                )
        )

        Mockito.`when`(
                restTemplate.getForEntity("${tvesUrl}/scale", TvesResponse::class.java)
        ).thenReturn(
                ResponseEntity(
                        TvesResponse(value = "83", unit = "кг.", code = "OK", message = "OK"),
                        HttpStatus.OK
                )
        )

    }


    @Test
    fun addTaskTest() {
        mvc.perform(
                post("/api/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"Documents\"}")
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Documents.name)))
                .andExpect(jsonPath("$.status", `is`(TaskStatus.Await.name)))
    }

    @Test
    fun takeHeightTest() {
        val res = mvc.perform(
               post("/api/task/add")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"type\": \"Height\"}")
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Height.name)))
                .andExpect(jsonPath("$.status", `is`(`in`(listOf(TaskStatus.Await.name, TaskStatus.Complete.name)))))
                .andReturn()
        val mapper = ObjectMapper()
        val task = mapper.readValue(res.response.contentAsString, Task::class.java)
        mvc.perform(get("/api/task/status/${task.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", `is`(TaskStatus.Complete.name)))

        mvc.perform(get("/api/task/result/${task.id}"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", `is`(task.id)))
                .andExpect(jsonPath("$.status", `is`(TaskStatus.Complete.name)))
                .andExpect(jsonPath("$.type", `is`(TaskType.Height.name)))
                .andExpect(jsonPath("$.result.value", `is`("179")))
                .andExpect(jsonPath("$.result.unit", `is`("см.")))
                .andExpect(jsonPath("$.result.code", `is`("OK")))
                .andExpect(jsonPath("$.result.message", `is`("OK")))
    }

    @Test
    fun takeWeightTest() {
        val res = mvc.perform(
                post("/api/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"Weight\"}")
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Weight.name)))
                .andExpect(jsonPath("$.status", `is`(`in`(listOf(TaskStatus.Await.name, TaskStatus.Complete.name)))))
                .andReturn()
        val mapper = ObjectMapper()
        val task = mapper.readValue(res.response.contentAsString, Task::class.java)
        mvc.perform(get("/api/task/status/${task.id}"))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", `is`(TaskStatus.Complete.name)))

        mvc.perform(get("/api/task/result/${task.id}"))
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", `is`(task.id)))
                .andExpect(jsonPath("$.type", `is`(TaskType.Weight.name)))
                .andExpect(jsonPath("$.status", `is`(TaskStatus.Complete.name)))
                .andExpect(jsonPath("$.result.value", `is`("83")))
                .andExpect(jsonPath("$.result.unit", `is`("кг.")))
                .andExpect(jsonPath("$.result.code", `is`("OK")))
                .andExpect(jsonPath("$.result.message", `is`("OK")))
    }
}
