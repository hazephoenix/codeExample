delete from valueset r where r.resource ->> 'url' = 'ValueSet/Config_codes';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Config_codes';

select resource_create('{"resourceType": "ValueSet", "id": "Config_codes", "description": "Коды настроек системы", "name": "Config_codes", "title": "Коды настроек системы", "status": "active", "url": "ValueSet/Config_codes"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Config_codes", "id": "Config_codes:RECALC_NEXT_OFFICE_IN_QUEUE", "code": "RECALC_NEXT_OFFICE_IN_QUEUE", "display": "Пересчитывать следующий кабинет в очереди"}'::jsonb);