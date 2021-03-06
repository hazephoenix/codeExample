delete from location where id like 'Office:%';

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:129",
  "name": "Смотровая",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "129", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Inspection", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{
  "resourceType": "Location",
  "id": "Office:139",
  "name": "Смотровая",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "139", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Inspection", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{
  "resourceType": "Location",
  "id": "Office:140",
  "name": "Смотровая",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "140", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Inspection", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:128",
  "name": "Перевязочный кабинет",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "128", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Перевязка", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:101",
  "name": "Процедурный кабинет",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "101", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Blood_analysis", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{
  "resourceType": "Location",
  "id": "Office:104",
  "name": "Процедурный кабинет",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "104", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Urine_analysis", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:130",
  "name": "ЭКГ",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "130", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "ECG", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:116",
  "name": "УЗИ",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "116", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Ultrasound", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:117",
  "name": "УЗИ",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "117", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Ultrasound", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:149",
  "name": "Бронхоскопия",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "149", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Bronchoscopy", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:150",
  "name": "Колоноскопия",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "150", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Colonoscopy", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:151",
  "name": "Гастроскопия",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "identifier": [{"value": "151", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Gastroscopy", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:202",
  "name": "Рентген",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "2 этаж"},
  "identifier": [{"value": "202", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "X-ray", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:119",
  "name": "Рентген",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "2 этаж"},
  "identifier": [{"value": "119", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "X-ray", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{
  "resourceType": "Location",
  "id": "Office:120",
  "name": "Компьютерная томография",
  "status": "BUSY",
  "address": {"use": "work", "type": "physical", "text": "2 этаж"},
  "identifier": [{"value": "120", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "type": [{"coding": [{"code": "Diagnostic", "system": "ValueSet/Location_types"}]}],
  "extension": {"observationType": [
      {"code": "Computer_tomography", "system": "ValueSet/Observation_types"}
  ],
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:RedZone",
  "name": "Красная зона",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "RedZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection1",
  "name": "Желтая зона. 1 смотровая",
  "identifier": [{"value": "1", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection2",
  "name": "Желтая зона. 2 смотровая",
  "identifier": [{"value": "2", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection3",
  "name": "Желтая зона. 3 смотровая",
  "identifier": [{"value": "3", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection4",
  "name": "Желтая зона. 4 смотровая",
  "identifier": [{"value": "4", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection5",
  "name": "Желтая зона. 5 смотровая",
  "identifier": [{"value": "5", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);
select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:YellowZoneSection6",
  "name": "Желтая зона. 6 смотровая",
  "identifier": [{"value": "6", "type": {"coding": [{"code": "OFFICE_NUMBER", "system": "ValueSet/Identifier_types", "display": "Номер кабинета"}]}}],
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "YellowZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);

select resource_create(('{"resourceType": "Location", "status": "BUSY",
  "id": "Office:GreenZone",
  "name": "Зеленая зона",
  "address": {"use": "work", "type": "physical", "text": "1 этаж"},
  "type": [{"coding": [{"code": "GreenZone", "system": "ValueSet/Location_types"}]}],
  "extension": {
    "nextOfficeForPatientsInfo": [],
    "statusUpdatedAt": "' || extract(epoch from CURRENT_TIMESTAMP)::bigint || '"
  }
}')::jsonb);