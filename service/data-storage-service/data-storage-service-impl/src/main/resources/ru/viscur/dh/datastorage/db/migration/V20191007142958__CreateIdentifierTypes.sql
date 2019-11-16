delete from valueset r where r.resource ->> 'url' = 'ValueSet/Identifier_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Identifier_types';

select resource_create('{"resourceType": "ValueSet", "id": "Identifier_types", "description": "Типы идетификаторов Identifier", "name": "Identifier_types", "title": "Типы идетификаторов Identifier", "status": "active", "url": "ValueSet/Identifier_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:RESOURCE", "code": "RESOURCE", "system": "ValueSet/Identifier_types", "display": "id ресурса"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:PASSPORT", "code": "PASSPORT", "system": "ValueSet/Identifier_types", "display": "Пасспорт"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:SNILS", "code": "SNILS", "system": "ValueSet/Identifier_types", "display": "СНИЛС"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:ENP", "code": "ENP", "system": "ValueSet/Identifier_types", "display": "ЕНП"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:DIGITAL_ASSURANCE", "code": "DIGITAL_ASSURANCE", "system": "ValueSet/Identifier_types", "display": "Полис в электронном виде"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:PAPER_ASSURANCE", "code": "PAPER_ASSURANCE", "system": "ValueSet/Identifier_types", "display": "Полис в бумажном виде"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:QUEUE_CODE", "code": "QUEUE_CODE", "system": "ValueSet/Identifier_types", "display": "Код, отображаемый в очереди"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:RFID", "code": "RFID", "system": "ValueSet/Identifier_types", "display": "Идентификатор RFID метки"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:CLAIM_NUMBER", "code": "CLAIM_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер обращения"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Identifier_types:OFFICE_NUMBER", "code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}'::jsonb);