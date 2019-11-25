package ru.viscur.dh.datastorage.api.util

/**
 * Created at 07.11.2019 16:09 by SherbakovaMA
 *
 * Константые значения в базе
 */

/**
 * Место приема пациентов фельдшером - фиктивный id
 */
const val RECEPTION = "RECEPTION"

/**
 * id кабинетов
 */
const val OFFICE_101 = "Office:101"
const val OFFICE_140 = "Office:140"
const val OFFICE_150 = "Office:150"
const val OFFICE_151 = "Office:151"
const val OFFICE_116 = "Office:116"
const val OFFICE_117 = "Office:117"
const val OFFICE_104 = "Office:104"
const val OFFICE_139 = "Office:139"
const val OFFICE_149 = "Office:149"
const val OFFICE_128 = "Office:128"
const val OFFICE_129 = "Office:129"
const val OFFICE_130 = "Office:130"
const val OFFICE_202 = "Office:202"
const val OFFICE_119 = "Office:119"
const val OFFICE_120 = "Office:120"

const val RED_ZONE = "Office:RedZone"
const val YELLOW_ZONE_SECTION_1 = "Office:YellowZoneSection1"
const val YELLOW_ZONE_SECTION_2 = "Office:YellowZoneSection2"
const val YELLOW_ZONE_SECTION_3 = "Office:YellowZoneSection3"
const val YELLOW_ZONE_SECTION_4 = "Office:YellowZoneSection4"
const val YELLOW_ZONE_SECTION_5 = "Office:YellowZoneSection5"
const val YELLOW_ZONE_SECTION_6 = "Office:YellowZoneSection6"
const val GREEN_ZONE = "Office:GreenZone"

val GROUP_1 = listOf(
        RECEPTION,
        OFFICE_128,
        RED_ZONE,
        YELLOW_ZONE_SECTION_1,
        YELLOW_ZONE_SECTION_2,
        YELLOW_ZONE_SECTION_3,
        YELLOW_ZONE_SECTION_4,
        YELLOW_ZONE_SECTION_5,
        YELLOW_ZONE_SECTION_6,
        GREEN_ZONE
)
val GROUP_2 = listOf(OFFICE_101, OFFICE_104)
val GROUP_3 = listOf(OFFICE_140, OFFICE_139, OFFICE_129, OFFICE_130)
val GROUP_4 = listOf(OFFICE_150, OFFICE_151, OFFICE_149)
val GROUP_5 = listOf(OFFICE_116, OFFICE_117)
val GROUP_6 = listOf(OFFICE_202)
val GROUP_7 = listOf(OFFICE_119, OFFICE_120)

val LOCATION_GROUPS = listOf(
        GROUP_1,
        GROUP_2,
        GROUP_3,
        GROUP_4,
        GROUP_5,
        GROUP_6,
        GROUP_7
)

/**
 * Код настройки Пересчитывать следующий кабинет в очереди
 */
const val RECALC_NEXT_OFFICE_CONFIG_CODE = "RECALC_NEXT_OFFICE_IN_QUEUE"

/**
 * Код категории услуг по забору крови
 */
const val BLOOD_ANALYSIS_CATEGORY = "Blood_analysis"

/**
 * Код категории услуг по забору мочи
 */
const val URINE_ANALYSIS_CATEGORY = "Urine_analysis"

/**
 * Типы услуг по забору мочи
 */
val URINE_ANALYSIS_TYPES = listOf(
        "B03.016.006",
        "A09.28.029"
)

/**
 * Код услуги "Перевязка"
 */
const val BANDAGE = "Перевязка"

/**
 * Код услуги "Первичный осмотр при регистрации обращения"
 */
const val INSPECTION_ON_RECEPTION = "Inspection_on_reception"

/**
 * Код услуги "Обращение в скорую помощь" (в целом: от регистрации до завершения)
 */
const val CLINICAL_IMPRESSION = "Clinical_impression"

/**
 * linkId вопроса опросника, в котором указывается степень тяжести пациента
 */
const val QUESTIONNAIRE_LINK_ID_SEVERITY = "Severity"

/**
 * linkId вопроса опросника, в котором указывается Канал поступления
 */
const val QUESTIONNAIRE_LINK_ID_ENTRY_TYPE = "Entry_type"

/**
 * linkId вопроса опросника, в котором указывается тип транспортировка
 */
const val QUESTIONNAIRE_LINK_ID_TRANSPORTATION_TYPE = "Transportation_type"

/**
 * id опросника с общей информацией
 */
const val QUESTIONNAIRE_ID_COMMON_INFO = "Common_info"

/**
 * Квалификации (специальности) врачей
 */
const val QUALIFICATION_THERAPIST = "Therapist"
const val QUALIFICATION_SURGEON = "Surgeon"
const val QUALIFICATION_NEUROLOGIST = "Neurologist"
const val QUALIFICATION_UROLOGIST = "Urologist"
const val QUALIFICATION_GYNECOLOGIST = "Gynecologist"
const val QUALIFICATION_PARAMEDIC = "Paramedic"
const val QUALIFICATION_DIAGNOSTIC_ASSISTANT = "Diagnostic_assistant"