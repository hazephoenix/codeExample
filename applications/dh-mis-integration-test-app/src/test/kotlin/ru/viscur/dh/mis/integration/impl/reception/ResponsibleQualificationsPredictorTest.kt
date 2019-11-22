package ru.viscur.dh.mis.integration.impl.reception

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.apps.misintegrationtest.service.ForTestService
import ru.viscur.dh.datastorage.api.util.*
import ru.viscur.dh.datastorage.impl.utils.ResponsibleQualificationsPredictor
import ru.viscur.dh.fhir.model.enums.Gender
import java.util.stream.Stream

/**
 * Created at 21.11.2019 10:21 by SherbakovaMA
 *
 * Тест для [ResponsibleQualificationsPredictor]
 */
@SpringBootTest(
        classes = [MisIntegrationTestConfig::class]
)
@EnableAutoConfiguration
@Disabled("Debug purposes only. Real codes could be different")
class ResponsibleQualificationsPredictorTest {

    @Autowired
    lateinit var responsibleQualificationsPredictor: ResponsibleQualificationsPredictor

    @Autowired
    lateinit var forTestService: ForTestService

    class TestCase(
            val desc: String,
            val diagnosis: String,
            val gender: Gender = Gender.female,
            val complaints: List<String> = listOf(),
            val exp: List<String>
    )

    companion object {
        private val testCases = listOf(
                TestCase(desc = "По полу. Мужчина идет к урологу",
                        diagnosis = "A50.9",
                        gender = Gender.male,
                        exp = listOf(
                                QUALIFICATION_UROLOGIST
                        )
                ),
                TestCase(desc = "По полу. Женщине может назначится уролог или гинеколог",
                        diagnosis = "A50.9",
                        gender = Gender.female,
                        exp = listOf(
                                QUALIFICATION_UROLOGIST,
                                QUALIFICATION_GYNECOLOGIST
                        )
                ),
                TestCase(desc = "Есть показательные жалобы для одного специалиста",
                        diagnosis = "I70.8",
                        complaints = listOf(
                                "присутствует острая БОЛЬ"
                        ),
                        exp = listOf(
                                QUALIFICATION_SURGEON
                        )
                ),
                TestCase(desc = "Есть показательные жалобы для нескольких специалистов",
                        diagnosis = "M95.5",
                        complaints = listOf(
                                "присутствует острая БОЛЬ",
                                "лихорадит"
                        ),
                        exp = listOf(
                                QUALIFICATION_SURGEON,
                                QUALIFICATION_THERAPIST
                        )
                ),
                TestCase(desc = "Нет показательных жалоб, есть специальность без показ. жалоб",
                        diagnosis = "M95.5",
                        complaints = listOf(
                                "боль в правом подреберье"
                        ),
                        exp = listOf(
                                QUALIFICATION_NEUROLOGIST
                        )
                ),
                TestCase(desc = "Нет показательных жалоб, все специалисты с показ. жалобами",
                        diagnosis = "A00.1",
                        complaints = listOf(
                                "боль в правом подреберье"
                        ),
                        exp = listOf(
                                QUALIFICATION_THERAPIST,
                                QUALIFICATION_SURGEON
                        )
                )
        )

        @JvmStatic
        private fun casesProvider(): Stream<Arguments> = Stream.of(*(testCases.map { Arguments.of(it) }.toTypedArray()))
    }

    @ParameterizedTest(name = "{index} => case={0}")
    @MethodSource("casesProvider")
    fun test(case: TestCase) {
        Assertions.assertLinesMatch(case.exp.sorted(), responsibleQualificationsPredictor.predict(case.diagnosis, case.gender.name, case.complaints).sorted(), case.desc)
    }
}