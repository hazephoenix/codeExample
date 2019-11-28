delete from valueset r where r.resource ->> 'url' = 'ValueSet/Upper_respiratory_airway';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Upper_respiratory_airway';

select resource_create('{"resourceType": "ValueSet", "id": "Upper_respiratory_airway", "url": "ValueSet/Upper_respiratory_airway", "name": "Upper_respiratory_airway", "title": "Результат осмотра верхних дыхательных путей", "description": "Результат осмотра верхних дыхательных путей", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Upper_respiratory_airway:1.Airways_not_passable_(asphyxia)_or_not_breathing", "code": "Airways_not_passable_(asphyxia)_or_not_breathing", "system": "ValueSet/Upper_respiratory_airway", "display": "Дыхательные пути не проходимы (асфиксия) или не дышит"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Upper_respiratory_airway:2.Airways_passable", "code": "Airways_passable", "system": "ValueSet/Upper_respiratory_airway", "display": "Дыхательные пути проходимы"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Breathing_rate';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Breathing_rate';

select resource_create('{"resourceType": "ValueSet", "id": "Breathing_rate", "url": "ValueSet/Breathing_rate", "name": "Breathing_rate", "title": "Частота дыхания", "description": "Частота дыхания", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Breathing_rate:1.More_than_30", "code": "More_than_30", "system": "ValueSet/Breathing_rate", "display": "Более 30"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Breathing_rate:2.From_25_to_30", "code": "From_25_to_30", "system": "ValueSet/Breathing_rate", "display": "От 25 до 30"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Breathing_rate:3.Less_than_25", "code": "Less_than_25", "system": "ValueSet/Breathing_rate", "display": "До 25"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Blood_oxygenation_level';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Blood_oxygenation_level';

select resource_create('{"resourceType": "ValueSet", "id": "Blood_oxygenation_level", "url": "ValueSet/Blood_oxygenation_level", "name": "Blood_oxygenation_level", "title": "Уровень оксигенации крови", "description": "Уровень оксигенации крови", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Blood_oxygenation_level:1.Less_than_90%_with_oxygen_inhalation", "code": "Less_than_90%_with_oxygen_inhalation", "system": "ValueSet/Blood_oxygenation_level", "display": "Менее 90% при ингаляции кислорода"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Blood_oxygenation_level:2.More_than_90%_with_oxygen_inhalation", "code": "More_than_90%_with_oxygen_inhalation", "system": "ValueSet/Blood_oxygenation_level", "display": "Более 90% с ингаляцией кислорода"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Blood_oxygenation_level:3.More_than_95%_without_oxygen_inhalation", "code": "More_than_95%_without_oxygen_inhalation", "system": "ValueSet/Blood_oxygenation_level", "display": "Более 95% без ингаляции кислорода"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Heart_rate';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Heart_rate';

select resource_create('{"resourceType": "ValueSet", "id": "Heart_rate", "url": "ValueSet/Heart_rate", "name": "Heart_rate", "title": "Частота сердечных сокращений в минуту", "description": "Частота сердечных сокращений в минуту", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Heart_rate:1.More_than_150_or_less_than_40", "code": "More_than_150_or_less_than_40", "system": "ValueSet/Heart_rate", "display": "Более 150 или менее 40"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Heart_rate:2.More_than_120_and_less_than_50", "code": "More_than_120_and_less_than_50", "system": "ValueSet/Heart_rate", "display": "Более 120 и менее 50"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Heart_rate:3.From_51_to_119", "code": "From_51_to_119", "system": "ValueSet/Heart_rate", "display": "От 51 до 119"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Blood_pressure_upper_limit';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Blood_pressure_upper_limit';

select resource_create('{"resourceType": "ValueSet", "id": "Blood_pressure_upper_limit", "url": "ValueSet/Blood_pressure_upper_limit", "name": "Blood_pressure_upper_limit", "title": "Артериальное давление нижняя граница", "description": "Артериальное давление нижняя граница", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Blood_pressure_upper_limit:1.Less_than_90", "code": "Less_than_90", "system": "ValueSet/Blood_pressure_upper_limit", "display": "Менее 90"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Blood_pressure_upper_limit:2.More_than_90", "code": "More_than_90", "system": "ValueSet/Blood_pressure_upper_limit", "display": "Более 90"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Consciousness_assessment';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Consciousness_assessment';

select resource_create('{"resourceType": "ValueSet", "id": "Consciousness_assessment", "url": "ValueSet/Consciousness_assessment", "name": "Consciousness_assessment", "title": "Оценка уровня сознания", "description": "Оценка уровня сознания", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:1.Coma,_ongoing_generalized_cramps", "code": "Coma,_ongoing_generalized_cramps", "system": "ValueSet/Consciousness_assessment", "display": "Кома, продолжающиеся генерализованные судороги"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:2.Stun", "code": "Stun", "system": "ValueSet/Consciousness_assessment", "display": "Оглушение, сопор"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consciousness_assessment:3.Clear_mind", "code": "Clear_mind", "system": "ValueSet/Consciousness_assessment", "display": "Ясное сознание"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Body_temperature';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Body_temperature';

select resource_create('{"resourceType": "ValueSet", "id": "Body_temperature", "url": "ValueSet/Body_temperature", "name": "Body_temperature", "title": "Температура тела", "description": "Температура тела", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Body_temperature:1.More_than_41_or_less_than_35", "code": "More_than_41_or_less_than_35", "system": "ValueSet/Body_temperature", "display": "Более 41 или менее 35"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Body_temperature:2.From_38.5_to_41", "code": "From_38.5_to_41", "system": "ValueSet/Body_temperature", "display": "От 38,5 до 41"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Body_temperature:3.From_35.1_to_38.4", "code": "From_35.1_to_38.4", "system": "ValueSet/Body_temperature", "display": "От 35,1 до 38,4"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Pain_intensity_assessment';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Pain_intensity_assessment';

select resource_create('{"resourceType": "ValueSet", "id": "Pain_intensity_assessment", "url": "ValueSet/Pain_intensity_assessment", "name": "Pain_intensity_assessment", "title": "Оценка интенсивности боли", "description": "Оценка интенсивности боли", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Pain_intensity_assessment:1.Not_count", "code": "Not_count", "system": "ValueSet/Pain_intensity_assessment", "display": "Не учитывается"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Pain_intensity_assessment:2.From_4_to_10", "code": "From_4_to_10", "system": "ValueSet/Pain_intensity_assessment", "display": "4-10"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Pain_intensity_assessment:3.From_0_to_3", "code": "From_0_to_3", "system": "ValueSet/Pain_intensity_assessment", "display": "0-3"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Patient_can_stand';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Patient_can_stand';

select resource_create('{"resourceType": "ValueSet", "id": "Patient_can_stand", "url": "ValueSet/Patient_can_stand", "name": "Patient_can_stand", "title": "Пациент может стоять", "description": "Пациент может стоять", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:1.Not_count", "code": "Not_count", "system": "ValueSet/Patient_can_stand", "display": "Не учитывается"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:2.Cant_stand", "code": "Cant_stand", "system": "ValueSet/Patient_can_stand", "display": "Не может стоять"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Patient_can_stand:3.Can_stand", "code": "Can_stand", "system": "ValueSet/Patient_can_stand", "display": "Может стоять"}'::jsonb);

delete from valueset r where r.resource ->> 'url' = 'ValueSet/Severity';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Severity';

select resource_create('{"resourceType": "ValueSet", "id": "Severity", "url": "ValueSet/Severity", "name": "Severity", "title": "Результат сортировки", "description": "Результат сортировки", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:1.RED", "code": "RED", "system": "ValueSet/Severity", "display": "Красный"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:2.YELLOW", "code": "YELLOW", "system": "ValueSet/Severity", "display": "Желтый"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Severity:3.GREEN", "code": "GREEN", "system": "ValueSet/Severity", "display": "Зеленый"}'::jsonb);