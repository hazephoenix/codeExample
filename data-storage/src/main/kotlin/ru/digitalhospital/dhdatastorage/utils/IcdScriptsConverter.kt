package ru.digitalhospital.dhdatastorage.utils

import java.io.File

/**
 * Created at 30.09.2019 15:10 by SherbakovaMA
 *
 * Конвертер sql запросов insert-ов в запросы добавления ресурсов в базу
 * из /import/icd10.sql в /import/out_icd10.sql. Получаются скрипты для миграций заполнения справочника ICD-10
 */
class IcdScriptsConverter {

    fun convert() {
        val fileContent = javaClass.getResource("/import/icd10.sql").readText()

        val outputLines =
                (listOf("{\"resourceType\": \"ValueSet\", \"id\": \"ICD-10\",  \"description\": \"This value set includes all ICD-10 codes.\", \"name\": \"ICD-10\", \"title\": \"МКБ-10\", \"status\": \"active\", \"url\": \"ValueSet/ICD-10\"}") +
                fileContent.split("\n").mapNotNull { line ->
            //                        id, name, code, parent_id, parent_code, node_count, additional_info
            val regex = ".* VALUES\\([^,]*, '([^']*)', '([^']*)', [^,]*, '([^']*)', [^,]*, ('[^']*'|NULL)".toRegex()
            val groups = regex.find(line)?.groups ?: return@mapNotNull null
            val name = getMatchGroup(line, groups, 1).replace("\"", "\\\"")
            val code = getMatchGroup(line, groups, 2)
            val parentCode = getMatchGroup(line, groups, 3)
            val additionalInfoTemp = getMatchGroup(line, groups, 4)
            //val additionalInfo = if (additionalInfoTemp == "NULL") null else additionalInfoTemp.replace("'", "")
            val additionalInfo = if (additionalInfoTemp == "NULL") "" else ", \"additionalInfo\": \"" + additionalInfoTemp.replace("'", "").replace("\"", "\\\"") +"\""


            //println("icd item: { name: $name, code: $code, parentCode: $parentCode, additionalInfo: $additionalInfo }")
            return@mapNotNull "{\"resourceType\": \"Concept\", \"id\": \"ICD-10:$code\",  \"code\": \"$code\",  \"parentCode\": \"$parentCode\",  \"system\": \"ValueSet/ICD-10\",  \"display\": \"$name\"$additionalInfo}"

        }).map{"SELECT fhirbase_create('$it'::jsonb);"}
        // в \build\resources\main\import\out_icd10.sql
        val outFile = File(javaClass.getResource("/import/out_icd10.sql").file)
        if (!outFile.exists()) {
            outFile.createNewFile()
        }
        outFile.writeText(outputLines.joinToString("\n"))
    }

    private fun getMatchGroup(line: String, groups: MatchGroupCollection, groupNumber: Int): String {
        val matchGroup = groups[groupNumber] ?: throw Exception("not found $groupNumber-group in $line")
        return line.substring(matchGroup.range)
    }
}