delete from Questionnaire r where r.id = 'Injury_info';

select resource_create('{"resourceType": "Questionnaire", "id": "Injury_info", "name": "Injury info", "title": "Информация о травме", "status": "active", "item": [
    {"linkId": "Injury_received_at_work", "text": "Травма получена на производстве", "type": "boolean"},
    {"linkId": "Injury_received_in_road_accident", "text": "Травма получена в ДТП", "type": "boolean"},
    {"linkId": "Injury_received_in_a_criminal_act", "text": "Травма получена в следствии криминального деяния", "type": "boolean"},
    {"linkId": "Injured_in_a_fire", "text": "Пострадал при пожаре", "type": "boolean"},
    {"linkId": "External_cause", "text": "Внешняя причина", "type": "string"},
    {"linkId": "Hours_after_injury", "text": "Часов после травмы прошло", "type": "integer"},
    {"linkId": "Hours_after_pain_appearance", "text": "Количество часов от начала боли", "type": "integer"},
    {"linkId": "Hours_after_symptoms_appearance", "text": "Количество часов от начала симптомов", "type": "integer"},
    {"linkId": "Medical_history", "text": "Анамнез болезни", "type": "string"},
    {"linkId": "Notification_number", "text": "Номер извещения", "type": "string"},
    {"linkId": "Entry_department", "text": "Отделение поступления", "type": "string"},
    {"linkId": "Disease_term_type", "text": "Характер заблевания",  "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Firsly", "display": "Впервые в жизни установленное", "system": "ValueSet/Disease_term_types"}},
        {"valueCoding": {"code": "Chronical", "display": "Хроническое", "system": "ValueSet/Disease_term_types"}},
        {"valueCoding": {"code": "Last_year_or_earlier", "display": "Диагноз установлен в предыдущем году или ранее", "system": "ValueSet/Disease_term_types"}},
        {"valueCoding": {"code": "Terebrant", "display": "Острое", "system": "ValueSet/Disease_term_types"}}
    ]}
]}'::jsonb);
