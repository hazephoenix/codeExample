delete from valueset r where r.resource ->> 'url' = 'ValueSet/Entry_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Entry_types';

select resource_create('{"resourceType": "ValueSet", "id": "Entry_types", "description": "Каналы поступления", "name": "Entry_types", "title": "Каналы поступления", "status": "active", "url": "ValueSet/Entry_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Entry_types:Personal_encounter", "code": "Personal_encounter", "system": "ValueSet/Entry_types", "display": "Самообращение"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Entry_types:Emergency", "code": "Emergency", "system": "ValueSet/Entry_types", "display": "Скорая помощь"}'::jsonb);