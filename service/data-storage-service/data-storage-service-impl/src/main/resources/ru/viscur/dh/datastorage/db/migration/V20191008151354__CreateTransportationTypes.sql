delete from valueset r where r.resource ->> 'url' = 'ValueSet/Transportation_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Transportation_types';

select resource_create('{"resourceType": "ValueSet", "id": "Transportation_types", "description": "Типы транспортировки", "name": "Transportation_types", "title": "Типы транспортировки", "status": "active", "url": "ValueSet/Transportation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Transportation_types:Personal", "code": "Personal", "system": "ValueSet/Transportation_types", "display": "Самостоятельно"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Transportation_types:Sitting", "code": "Sitting", "system": "ValueSet/Transportation_types", "display": "Сидя"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Transportation_types:Lying", "code": "Lying", "system": "ValueSet/Transportation_types", "display": "Лежа"}'::jsonb);