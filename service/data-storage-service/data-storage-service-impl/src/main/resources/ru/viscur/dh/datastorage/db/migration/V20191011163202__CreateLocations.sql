select * from location where id like 'Office:%';

select resource_create('{
  "resourceType": "Location",
  "id": "Office:129",
  "name": "Cмотровая",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "129", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}]}]
}'::jsonb);
select resource_create('{
  "resourceType": "Location",
  "id": "Office:139",
  "name": "Cмотровая",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "139", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}]}]
}'::jsonb);
select resource_create('{
  "resourceType": "Location",
  "id": "Office:140",
  "name": "Cмотровая",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "140", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "ViewingRoom", "system": "ValueSet/Location_types", "display": "Cмотровая"}]}]
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:101",
  "name": "Процедурный кабинет",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "101", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "TreatmentRoom", "system": "ValueSet/Location_types", "display": "Процедурный кабинет"}]}],
  "extension": {"observationType": [
      {"code": "B03.016.004ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "A09.05.036.001ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "A09.20.003ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "A09.05.049.01ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "B03.016.002ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "B03.016.004ГМУ_СП_ТК", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);
select resource_create('{
  "resourceType": "Location",
  "id": "Office:104",
  "name": "Процедурный кабинет",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "104", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "TreatmentRoom", "system": "ValueSet/Location_types", "display": "Процедурный кабинет"}]}],
  "extension": {"observationType": [
      {"code": "B03.016.006ГМУ_СП", "system": "ValueSet/Observation_types"},
      {"code": "A09.28.029ГМУ_СП", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:130",
  "name": "Функционально-диагностический кабинет",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "130", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "FunctionalDiagnostics", "system": "ValueSet/Location_types", "display": "Функциональная диагностика"}]}],
  "extension": {"observationType": [
      {"code": "ECG", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:116",
  "name": "УЗИ",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "116", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "UltrasoundDiagnostics", "system": "ValueSet/Location_types", "display": "Ультразвуковая диагностика"}]}],
  "extension": {"observationType": [
      {"code": "Ultrasound", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:117",
  "name": "УЗИ",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "117", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "UltrasoundDiagnostics", "system": "ValueSet/Location_types", "display": "Ультразвуковая диагностика"}]}],
  "extension": {"observationType": [
      {"code": "Ultrasound", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:149",
  "name": "Эндоскопия (Бронхоскопия)",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "149", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}]}]
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:150",
  "name": "Эндоскопия (Колоноскопия)",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "150", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}]}]
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:151",
  "name": "Эндоскопия (ЭГДС)",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "151", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Endoscopy", "system": "ValueSet/Location_types", "display": "Эндоскопия"}]}]
}'::jsonb);

select resource_create('{
  "resourceType": "Location",
  "id": "Office:202",
  "name": "Рентген",
  "status": "READY",
  "address": {"use": "work", "type": "physical", "text": "2 этаж"},
  "identifier": [{"value": "202", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "GeneralRadiology", "system": "ValueSet/Location_types", "display": "Общая рентгенология"}]}],
  "extension": {"observationType": [
      {"code": "X-ray", "system": "ValueSet/Observation_types"}
  ]}
}'::jsonb);

select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:RedZone",
  "name": "Красная зона",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "RedZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);

select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection1",
  "name": "Желтая зона. 1 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);
select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection2",
  "name": "Желтая зона. 2 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);
select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection3",
  "name": "Желтая зона. 3 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);
select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection4",
  "name": "Желтая зона. 4 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);
select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection5",
  "name": "Желтая зона. 5 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);
select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:YellowZoneSection6",
  "name": "Желтая зона. 6 смотровая",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);

select resource_create('{"resourceType": "Location", "status": "READY",
  "id": "Office:GreenZone",
  "name": "Зеленая зона",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "GreenZone", "system": "ValueSet/Location_types"}]}]
}'::jsonb);