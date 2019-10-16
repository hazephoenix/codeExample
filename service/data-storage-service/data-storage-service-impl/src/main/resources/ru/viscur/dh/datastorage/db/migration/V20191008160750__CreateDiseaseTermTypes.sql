delete from valueset r where r.resource ->> 'url' = 'ValueSet/Disease_term_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Disease_term_types';

select resource_create('{"resourceType": "ValueSet", "id": "Disease_term_types", "name": "Disease_term_types", "title": "Характер заболевания", "description": "Классификация по продолжительности заболевания", "status": "active", "url": "ValueSet/Disease_term_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Disease_term_types:Firsly", "code": "Firsly", "system": "ValueSet/Disease_term_types", "display": "Впервые в жизни установленное"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Disease_term_types:Chronical", "code": "Chronical", "system": "ValueSet/Disease_term_types", "display": "Хроническое"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Disease_term_types:Last_year_or_earlier", "code": "Last_year_or_earlier", "system": "ValueSet/Disease_term_types", "display": "Диагноз установлен в предыдущем году или ранее"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Disease_term_types:Terebrant", "code": "Terebrant", "system": "ValueSet/Disease_term_types", "display": "Острое"}'::jsonb);