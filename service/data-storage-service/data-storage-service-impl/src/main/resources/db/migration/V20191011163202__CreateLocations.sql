delete from location;

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/129",
  "name": "Cмотровая",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "129", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}
}'::jsonb);
select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/139",
  "name": "Cмотровая",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "139", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}
}'::jsonb);
select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/140",
  "name": "Cмотровая",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "140", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/101",
  "name": "Процедурный кабинет",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "101", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "TreatmentRoom", "system": "ValueSet/Location_types", "display": "Процедурный кабинет"}
}'::jsonb);
select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/104",
  "name": "Процедурный кабинет",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "104", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "TreatmentRoom", "system": "ValueSet/Location_types", "display": "Процедурный кабинет"}
}'::jsonb);




select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/130",
  "name": "Функционально-диагностический кабинет",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "130", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "FunctionalDiagnostics", "system": "ValueSet/Location_types", "display": "Функциональная диагностика"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/117",
  "name": "УЗИ",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "117", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "UltrasoundDiagnostics", "system": "ValueSet/Location_types", "display": "Ультразвуковая диагностика"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/149",
  "name": "Эндоскопия (Бронхоскопия)",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "149", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/150",
  "name": "Эндоскопия (Колоноскопия)",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "150", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/151",
  "name": "Эндоскопия (ЭГДС)",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": {"value": "151", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}
}'::jsonb);

select fhirbase_create('{
  "resourceType": "Location",
  "id": "Location/202",
  "name": "Рентген",
  "status": "active",
  "address": {"use": "work", "type": "physical", "text": "2 этаж"},
  "identifier": {"value": "202", "type": {"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}},
  "type": {"code": "GeneralRadiology", "system": "ValueSet/Location_types", "display": "Общая рентгенология"}
}'::jsonb);