delete from valueset r where r.resource ->> 'url' = 'ValueSet/Observation_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Observation_types';

select resource_create('{"resourceType": "ValueSet", "id": "Observation_types", "url": "ValueSet/Observation_types", "description": "Типы процедур/услуг", "name": "Observation_types", "title": "Типы процедур/услуг", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Vital_signs", "code": "Vital_signs", "parentCode": null, "display": "Жизненно важные физиологические показатели", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_upper_level", "code": "Blood_pressure_upper_level", "parentCode": "Vital_signs", "display": "Артериальное давление верхняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_lower_level", "code": "Blood_pressure_lower_level", "parentCode": "Vital_signs", "display": "Артериальное давление нижняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Heart_rate", "code": "Heart_rate", "display": "Частота сердечных сокращений", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Breathing_rate", "code": "Breathing_rate", "display": "Частота дыхания", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Body_temperature", "code": "Body_temperature", "display": "Температура тела", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Electrocardiogram", "code": "Electrocardiogram", "display": "Электрокардиограмма", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Degree_of_saturation_of_blood_with_oxygen", "code": "Degree_of_saturation_of_blood_with_oxygen", "parentCode": "Vital_signs", "display": "Степень насыщения крови кислородом", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Weight", "code": "Weight", "parentCode": "Vital_signs", "display": "Вес", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Height", "code": "Height", "parentCode": "Vital_signs", "display": "Рост", "system": "ValueSet/Observation_types"}'::jsonb);

select resource_create('{"resourceType": "Concept", "id": "Observation_types:Inspections", "code": "Inspections", "parentCode": null, "display": "Осмотры (Черновик)", "system": "ValueSet/Observation_types", "priority": "0.25"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Surgeon", "code": "Surgeon", "parentCode": "Inspections", "display": "Осмотр хирурга", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Therapist", "code": "Therapist", "parentCode": "Inspections", "display": "Осмотр терапевта", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Urologist", "code": "Urologist", "parentCode": "Inspections", "display": "Осмотр уролога", "system": "ValueSet/Observation_types"}'::jsonb);

select resource_create('{"resourceType": "Concept", "id": "Observation_types:Diagnostic", "code": "Diagnostic", "parentCode": null, "display": "Диагностические (Черновик)", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Ultrasound_of_the_kidneys", "code": "Ultrasound_of_the_kidneys", "parentCode": "Diagnostic", "display": "УЗИ почек", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Ultrasound_of_the_heart", "code": "Ultrasound_of_the_heart", "parentCode": "Diagnostic", "display": "УЗИ сердца", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:X_ray_of_the_leg", "code": "X_ray_of_the_leg", "parentCode": "Diagnostic", "display": "Рентген ноги", "system": "ValueSet/Observation_types"}'::jsonb);

select resource_create('{"resourceType": "Concept", "id": "Observation_types:Analysis", "code": "Analysis", "parentCode": null, "display": "Анализы (Черновик)", "system": "ValueSet/Observation_types", "priority": "0.75"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:OAK", "code": "OAK", "parentCode": "Analysis", "display": "ОАК", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:OAM", "code": "OAM", "parentCode": "Analysis", "display": "ОАМ", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Bio", "code": "Bio", "parentCode": "Analysis", "display": "Биохимия", "system": "ValueSet/Observation_types"}'::jsonb);
