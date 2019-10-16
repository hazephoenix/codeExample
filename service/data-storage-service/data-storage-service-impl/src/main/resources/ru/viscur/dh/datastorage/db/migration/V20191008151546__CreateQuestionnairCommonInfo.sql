delete from Questionnaire r where r.id = 'Common_info';

select resource_create('{"resourceType": "Questionnaire", "id": "Common_info", "name": "Common info", "title": "Общая информация об обращении", "status": "active", "item": [
    {"linkId": "WWII_participant", "text": "Участник ВОВ", "type": "boolean"},
    {"linkId": "Chernobyl_resident", "text": "Чернобылец", "type": "boolean"},
    {"linkId": "Complaints", "text": "Жалобы", "type": "string"},
    {"linkId": "Entry_type", "text": "Канал поступления",  "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Personal_encounter", "display": "Самообращение", "system": "ValueSet/Entry_types"}},
        {"valueCoding": {"code": "Emergency", "display": "Скорая помощь", "system": "ValueSet/Entry_types"}}
    ]},
    {"linkId": "Main_syndrome", "text": "Ведущий синдром", "type": "string"},
    {"linkId": "Pregnancy", "text": "Беременность", "type": "boolean"}
]}'::jsonb);