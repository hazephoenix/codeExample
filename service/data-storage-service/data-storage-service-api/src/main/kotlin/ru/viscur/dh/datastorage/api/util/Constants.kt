package ru.viscur.dh.datastorage.api.util

import ru.viscur.dh.fhir.model.enums.Severity

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
const val OFFICE_129 = "Office:129"
const val OFFICE_130 = "Office:130"
const val OFFICE_202 = "Office:202"

const val RED_ZONE = "Office:RedZone"
const val YELLOW_ZONE_SECTION_1 = "Office:YellowZoneSection1"
const val YELLOW_ZONE_SECTION_2 = "Office:YellowZoneSection2"
const val YELLOW_ZONE_SECTION_3 = "Office:YellowZoneSection3"
const val YELLOW_ZONE_SECTION_4 = "Office:YellowZoneSection4"
const val YELLOW_ZONE_SECTION_5 = "Office:YellowZoneSection5"
const val YELLOW_ZONE_SECTION_6 = "Office:YellowZoneSection6"
const val GREEN_ZONE = "Office:GreenZone"

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
 * Код услуги "Первичный осмотр при регистрации обращения"
 */
const val INSPECTION_ON_RECEPTION = "Inspection_on_reception"

/**
 * Код услуги "Обращение в скорую помощь" (в целом: от регистрации до завершения)
 */
const val CLINICAL_IMPRESSION = "Clinical_impression"

/**
 * linkId опросника, в котором указывается степень тяжести пациента
 */
const val QUESTIONNAIRE_LINK_ID_SEVERITY = "Severity"