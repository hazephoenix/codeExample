delete from valueset r where r.resource ->> 'url' = 'ValueSet/Practitioner_qualifications';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Practitioner_qualifications';

select resource_create('{"resourceType": "ValueSet", "id": "Practitioner_qualifications", "description": "Специальности мед. работников", "name": "Practitioner_qualifications", "title": "Специальности мед. работников", "status": "active", "url": "ValueSet/Practitioner_qualifications"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Therapist", "code": "Therapist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-терапевт"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Surgeon", "code": "Surgeon", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-хирург"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Neurologist", "code": "Neurologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-невролог"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Urologist", "code": "Urologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-уролог"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Gynecologist", "code": "Gynecologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-гинеколог", "relativeGender": "female"}'::jsonb);