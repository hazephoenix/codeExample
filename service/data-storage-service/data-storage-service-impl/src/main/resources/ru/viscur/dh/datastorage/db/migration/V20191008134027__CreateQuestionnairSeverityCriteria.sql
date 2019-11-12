delete from Questionnaire r where r.id = 'Severity_criteria';

select resource_create('{"resourceType": "Questionnaire", "id": "Severity_criteria", "name": "Sorting criteria", "title": "Критерии сортировки", "status": "active", "item": [
    {"linkId": "Upper_respiratory_airway", "text": "Результат осмотра верхних дыхательных путей", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Airways_not_passable_(asphyxia)_or_not_breathing", "display": "Дыхательные пути не проходимы (асфиксия) или не дышит", "system": "ValueSet/Upper_respiratory_airway"}},
        {"valueCoding": {"code": "Airways_passable", "display": "Дыхательные пути проходимы", "system": "ValueSet/Upper_respiratory_airway"}}
    ]},
    {"linkId": "Breathing_rate", "text": "Частота дыхания", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "More_than_30", "display": "Более 30", "system": "ValueSet/Breathing_rate"}},
        {"valueCoding": {"code": "From_25_to_30", "display": "От 25 до 30", "system": "ValueSet/Breathing_rate"}},
        {"valueCoding": {"code": "Less_than_25", "display": "До 25", "system": "ValueSet/Breathing_rate"}}
    ]},
    {"linkId": "Blood_oxygenation_level", "text": "Уровень оксигенации крови", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Less_than_90%_with_oxygen_inhalation", "display": "Менее 90% при ингаляции кислорода", "system": "ValueSet/Blood_oxygenation_level"}},
        {"valueCoding": {"code": "More_than_90%_with_oxygen_inhalation", "display": "Более 90% с ингаляцией кислорода", "system": "ValueSet/Blood_oxygenation_level"}},
        {"valueCoding": {"code": "More_than_95%_without_oxygen_inhalation", "display": "Более 95% без ингаляции кислорода", "system": "ValueSet/Blood_oxygenation_level"}}
    ]},
    {"linkId": "Heart_rate", "text": "Частота сердечных сокращений в минуту", "type": "choice", "answerOption": [
      {"valueCoding": {"code": "More_than_150_or_less_than_40", "display": "Более 150 или менее 40", "system": "ValueSet/Heart_rate"}},
      {"valueCoding": {"code": "More_than_120_and_less_than_50", "display": "Более 120 и менее 50", "system": "ValueSet/Heart_rate"}},
      {"valueCoding": {"code": "From_51_to_119", "display": "От 51 до 119", "system": "ValueSet/Heart_rate"}}
    ]},
    {"linkId": "Blood_pressure_upper_limit", "text": "Артериальное давление нижняя граница", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Less_than_90", "display": "Менее 90", "system": "ValueSet/Blood_pressure_upper_limit"}},
        {"valueCoding": {"code": "More_than_90", "display": "Более 90", "system": "ValueSet/Blood_pressure_upper_limit"}}
    ]},
    {"linkId": "Consciousness_assessment", "text": "Оценка уровня сознания", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Coma,_ongoing_generalized_cramps", "display": "Кома, продолжающиеся генерализованные судороги", "system": "ValueSet/Consciousness_assessment"}},
        {"valueCoding": {"code": "Stun", "display": "Оглушение, сопор", "system": "ValueSet/Consciousness_assessment"}},
        {"valueCoding": {"code": "Clear_mind", "display": "Ясное сознание", "system": "ValueSet/Consciousness_assessment"}}
    ]},
    {"linkId": "Body_temperature", "text": "Температура тела", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "More_than_41_or_less_than_35", "display": "Более 41 или менее 35", "system": "ValueSet/Body_temperature"}},
        {"valueCoding": {"code": "From_38.5_to_41", "display": "От 38,5 до 41", "system": "ValueSet/Body_temperature"}},
        {"valueCoding": {"code": "From_35.1_to_38.4", "display": "От 35,1 до 38,4", "system": "ValueSet/Body_temperature"}}
    ]},
    {"linkId": "Pain_intensity_assessment", "text": "Оценка интенсивности боли", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Not_count", "display": "Не учитывается", "system": "ValueSet/Pain_intensity_assessment"}},
        {"valueCoding": {"code": "From_4_to_10", "display": "4-10", "system": "ValueSet/Pain_intensity_assessment"}},
        {"valueCoding": {"code": "From_0_to_3", "display": "0-3", "system": "ValueSet/Pain_intensity_assessment"}}
    ]},
    {"linkId": "Patient_can_stand", "text": "Пациент может стоять", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Not_count", "display": "Не учитывается", "system": "ValueSet/Patient_can_stand"}},
        {"valueCoding": {"code": "Cant_stand", "display": "Не может стоять", "system": "ValueSet/Patient_can_stand"}},
        {"valueCoding": {"code": "Can_stand", "display": "Может стоять", "system": "ValueSet/Patient_can_stand"}}
    ]},
    {"linkId": "Severity", "text": "Результат сортировки", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "RED", "display": "Красный", "system": "ValueSet/Severity"}},
        {"valueCoding": {"code": "YELLOW", "display": "Желтый", "system": "ValueSet/Severity"}},
        {"valueCoding": {"code": "GREEN", "display": "Зеленый", "system": "ValueSet/Severity"}}
    ]}
]}'::jsonb);
