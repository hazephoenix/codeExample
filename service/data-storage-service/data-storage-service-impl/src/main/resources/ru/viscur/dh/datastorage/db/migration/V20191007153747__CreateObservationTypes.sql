delete from valueset r where r.resource ->> 'url' = 'ValueSet/Observation_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Observation_types';

select resource_create('{"resourceType": "ValueSet", "id": "Observation_types", "url": "ValueSet/Observation_types", "description": "Типы процедур/услуг", "name": "Observation_types", "title": "Типы процедур/услуг", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Vital_signs", "code": "Vital_signs", "parent_code": null, "display": "Жизненно важные физиологические показатели", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_upper_level", "code": "Blood_pressure_upper_level", "parent_code": "Vital_signs", "display": "Артериальное давление верхняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_lower_level", "code": "Blood_pressure_lower_level", "parent_code": "Vital_signs", "display": "Артериальное давление нижняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Heart_rate", "code": "Heart_rate", "display": "Частота сердечных сокращений", "parent_code": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Breathing_rate", "code": "Breathing_rate", "display": "Частота дыхания", "parent_code": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Body_temperature", "code": "Body_temperature", "display": "Температура тела", "parent_code": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Electrocardiogram", "code": "Electrocardiogram", "display": "Электрокардиограмма", "parent_code": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Degree_of_saturation_of_blood_with_oxygen", "code": "Degree_of_saturation_of_blood_with_oxygen", "parent_code": "Vital_signs", "display": "Степень насыщения крови кислородом", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Weight", "code": "Weight", "parent_code": "Vital_signs", "display": "Вес", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Height", "code": "Height", "parent_code": "Vital_signs", "display": "Рост", "system": "ValueSet/Observation_types"}'::jsonb);
