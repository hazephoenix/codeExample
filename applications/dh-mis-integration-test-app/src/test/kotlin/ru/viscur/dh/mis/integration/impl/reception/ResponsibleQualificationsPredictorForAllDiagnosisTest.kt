package ru.viscur.dh.mis.integration.impl.reception

import jdk.nashorn.internal.ir.annotations.Ignore
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import ru.viscur.dh.apps.misintegrationtest.config.MisIntegrationTestConfig
import ru.viscur.dh.datastorage.api.ConceptService
import ru.viscur.dh.datastorage.impl.utils.ResponsibleQualificationsPredictor
import ru.viscur.dh.fhir.model.enums.Gender
import ru.viscur.dh.fhir.model.valueSets.ValueSetName

/**
 * Created at 21.11.2019 14:18 by SherbakovaMA
 *
 * Тест, что для ЛЮБОГО диагноза найдется "ответственная" специальность (или неск-ко)
 */
//@SpringBootTest(
//        classes = [MisIntegrationTestConfig::class]
//)
//@EnableAutoConfiguration
@Disabled("Debug purposes only. Test checks more than 10000 diagnosis - more than 15 minutes")
class ResponsibleQualificationsPredictorForAllDiagnosisTest {

    @Autowired
    lateinit var responsibleQualificationsPredictor: ResponsibleQualificationsPredictor

    @Autowired
    lateinit var conceptService: ConceptService

    @Test
    fun test() {
        val withoutResult = conceptService.allInLastLevel(ValueSetName.ICD_10).map { code ->
            val withoutResult = mutableListOf<String>()

//            try {
//                val actualForMale = responsibleQualificationsPredictor.predict(code, Gender.male.name, listOf())
//                if (actualForMale.isEmpty()) {
//                    withoutResult.add("$code (${Gender.male.name})")
//                }
//            } catch (e: Exception) {
//                withoutResult.add("$code (${Gender.male.name}) ${e.message}" )
//            }
            try {
                val actualForFemale = responsibleQualificationsPredictor.predict(code, Gender.female.name, listOf())
                if (actualForFemale.isEmpty()) {
                    withoutResult.add("$code (${Gender.female.name})")
                }
            } catch (e: Exception) {
                withoutResult.add("$code (${Gender.female.name})")
            }

            withoutResult
        }.flatten()
        Assertions.assertTrue(withoutResult.isEmpty(), "can't predict qualifications for diagnosis ${withoutResult.joinToString("\n")}")
    }
}