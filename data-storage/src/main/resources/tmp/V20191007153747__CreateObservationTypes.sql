delete from valueset r where r.resource ->> 'url' = 'ValueSet/Observation_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Observation_types';

select fhirbase_create('{"resourceType": "ValueSet", "id": "Observation_types", "description": "Типы процедур/услуг", "name": "Observation_types", "title": "Типы процедур/услуг", "status": "active", "url": "ValueSet/Observation_types"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Observation_types:PERSONAL_DATA", "code": "PERSONAL_DATA", "system": "ValueSet/Observation_types", "display": "Узи почек"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Observation_types:MEDICAL_INTERVENTION", "code": "MEDICAL_INTERVENTION", "system": "ValueSet/Observation_types", "display": "Осмотр хирурга"}'::jsonb);
