delete from Questionnaire r where r.id = 'Severity_criteria';

select resource_create('{"resourceType": "Questionnaire", "id": "Severity_criteria", "name": "Sorting criteria", "title": "Критерии сортировки", "status": "active", "item": [
    {"linkId": "Upper_respiratory_airway", "text": "Результат осмотра верхних дыхательных путей", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Airways_not_passable_(asphyxia)_or_not_breathing", "display": "Дыхательные пути не проходимы (асфиксия) или не дышит", "system": "ValueSet/Upper_respiratory_airway"}},
        {"valueCoding": {"code": "Airways_passable", "display": "Дыхательные пути проходимы", "system": "ValueSet/Upper_respiratory_airway"}}
    ]},
    {"linkId": "Consciousness_assessment", "text": "Оценка уровня сознания", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Coma,_ongoing_generalized_cramps", "display": "Кома, продолжающиеся генерализованные судороги", "system": "ValueSet/Consciousness_assessment"}},
        {"valueCoding": {"code": "Stun", "display": "Оглушение, сопор", "system": "ValueSet/Consciousness_assessment"}},
        {"valueCoding": {"code": "Clear_mind", "display": "Ясное сознание", "system": "ValueSet/Consciousness_assessment"}}
    ]},
    {"linkId": "Patient_can_stand", "text": "Пациент может стоять", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "Not_count", "display": "Не учитывается", "system": "ValueSet/Patient_can_stand"}},
        {"valueCoding": {"code": "Cant_stand", "display": "Не может стоять", "system": "ValueSet/Patient_can_stand"}},
        {"valueCoding": {"code": "Can_stand", "display": "Может стоять", "system": "ValueSet/Patient_can_stand"}}
    ]},
    {"linkId": "Complaints", "text": "Жалобы", "type": "reference"},
    {"linkId": "Severity", "text": "Результат сортировки", "type": "choice", "answerOption": [
        {"valueCoding": {"code": "RED", "display": "Красный", "system": "ValueSet/Severity"}},
        {"valueCoding": {"code": "YELLOW", "display": "Желтый", "system": "ValueSet/Severity"}},
        {"valueCoding": {"code": "GREEN", "display": "Зеленый", "system": "ValueSet/Severity"}}
    ]}
]}'::jsonb);
