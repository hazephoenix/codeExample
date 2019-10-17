delete from valueset r where r.resource ->> 'url' = 'ValueSet/Consent_categories';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Consent_categories';

select resource_create('{"resourceType": "ValueSet", "id": "Consent_categories", "description": "Типы согласий", "name": "Consent_categories", "title": "Типы согласий", "status": "active", "url": "ValueSet/Consent_categories"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consent_categories:PERSONAL_DATA", "code": "PERSONAL_DATA", "system": "ValueSet/Consent_categories", "display": "Согласие на обарботку персональных данных"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Consent_categories:MEDICAL_INTERVENTION", "code": "MEDICAL_INTERVENTION", "system": "ValueSet/Consent_categories", "display": "Согласие на медицинское вмешательство"}'::jsonb);
