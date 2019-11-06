package ru.viscur.dh.apps.paramedicdevice

import com.fasterxml.jackson.databind.ObjectMapper
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.web.client.RestTemplate
import ru.viscur.dh.apps.paramedicdevice.configuration.AppUID
import ru.viscur.dh.apps.paramedicdevice.dto.Task
import ru.viscur.dh.apps.paramedicdevice.dto.TaskStatus
import ru.viscur.dh.apps.paramedicdevice.dto.TaskType
import ru.viscur.dh.apps.paramedicdevice.dto.TvesResponse
import java.util.*
import java.util.regex.Pattern

/**
 * Created at 28.10.2019 14:39 by TimochkinEA
 */
@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class TaskApiTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @MockBean
    private lateinit var restTemplate: RestTemplate

    @Value("\${paramedic.tves.url:http://localhost:1221/tves}")
    private lateinit var tvesUrl: String

    @Autowired
    private lateinit var uid: AppUID

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
        val auth = "Basic ${Base64.getEncoder().encodeToString("${uid.uid}:${uid.apiPassword}".toByteArray())}"
        mvc.perform(
                post("/api/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"Document\"}")
                        .header(HttpHeaders.AUTHORIZATION, auth)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Document.name)))
                .andExpect(jsonPath("$.status", `is`(TaskStatus.InProgress.name)))
    }

    @Test
    fun takeHeightTest() {
        val auth = "Basic ${Base64.getEncoder().encodeToString("${uid.uid}:${uid.apiPassword}".toByteArray())}"
        val res = mvc.perform(
               post("/api/task/add")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content("{\"type\": \"Height\"}")
                       .header(HttpHeaders.AUTHORIZATION, auth)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Height.name)))
                .andExpect(jsonPath("$.status", `is`(`in`(listOf(TaskStatus.Await.name, TaskStatus.Complete.name, TaskStatus.InProgress.name)))))
                .andReturn()
        val mapper = ObjectMapper()
        val task = mapper.readValue(res.response.contentAsString, Task::class.java)
        mvc.perform(get("/api/task/status/${task.id}").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", `is`(TaskStatus.Complete.name)))

        mvc.perform(get("/api/task/result/${task.id}").header(HttpHeaders.AUTHORIZATION, auth))
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
        val auth = "Basic ${Base64.getEncoder().encodeToString("${uid.uid}:${uid.apiPassword}".toByteArray())}"
        val res = mvc.perform(
                post("/api/task/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"type\": \"Weight\"}")
                        .header(HttpHeaders.AUTHORIZATION, auth)
        )
                .andExpect(status().isOk)
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", matchesRegex(Pattern.compile("[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"))))
                .andExpect(jsonPath("$.type", `is`(TaskType.Weight.name)))
                .andExpect(jsonPath("$.status", `is`(`in`(listOf(TaskStatus.Await.name,  TaskStatus.InProgress.name, TaskStatus.Complete.name)))))
                .andReturn()
        val mapper = ObjectMapper()
        val task = mapper.readValue(res.response.contentAsString, Task::class.java)
        mvc.perform(get("/api/task/status/${task.id}").header(HttpHeaders.AUTHORIZATION, auth))
                .andExpect(status().isOk)
                .andExpect(jsonPath("$", `is`(TaskStatus.Complete.name)))

        mvc.perform(get("/api/task/result/${task.id}").header(HttpHeaders.AUTHORIZATION, auth))
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
