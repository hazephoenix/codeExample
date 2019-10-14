delete from valueset r where r.resource ->> 'url' = 'ValueSet/Location_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Location_types';

select fhirbase_create('{"resourceType": "ValueSet", "id": "Location_types", "description": "Типы кабинетов", "name": "Location_types", "title": "Типы кабинетов", "status": "active", "url": "ValueSet/Location_types"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:ViewingRoom", "code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:OperatingRoom", "code": "OperatingRoom", "system": "ValueSet/Location_types", "display": "Операционная"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:TreatmentRoom", "code": "TreatmentRoom", "system": "ValueSet/Location_types", "display": "Процедурный кабинет"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:Reception", "code": "Reception", "system": "ValueSet/Location_types", "display": "Приемная"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:UltrasoundDiagnostics", "code": "UltrasoundDiagnostics", "system": "ValueSet/Location_types", "display": "Ультразвуковая диагностика"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:FunctionalDiagnostics", "code": "FunctionalDiagnostics", "system": "ValueSet/Location_types", "display": "Функциональная диагностика"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:Endoscopy", "code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Location_types:GeneralRadiology", "code": "GeneralRadiology", "system": "ValueSet/Location_types", "display": "Общая рентгенология"}'::jsonb);