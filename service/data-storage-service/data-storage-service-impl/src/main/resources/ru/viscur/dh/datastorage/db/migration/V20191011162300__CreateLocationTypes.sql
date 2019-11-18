delete from valueset r where r.resource ->> 'url' = 'ValueSet/Location_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Location_types';

select resource_create('{"resourceType": "ValueSet", "id": "Location_types", "description": "Типы кабинетов", "name": "Location_types", "title": "Типы кабинетов", "status": "active", "url": "ValueSet/Location_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Location_types:Inspection", "code": "Inspection", "system": "ValueSet/Location_types", "display": "Смотровая"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Location_types:Diagnostic", "code": "Diagnostic", "system": "ValueSet/Location_types", "display": "Кабинет диагностики/процедурный кабинет"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Location_types:RedZone", "code": "RedZone", "system": "ValueSet/Location_types", "display": "Красная зона"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Location_types:YellowZone", "code": "YellowZone", "system": "ValueSet/Location_types", "display": "Желтая зона"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Location_types:GreenZone", "code": "GreenZone", "system": "ValueSet/Location_types", "display": "Зеленая зона"}'::jsonb);
