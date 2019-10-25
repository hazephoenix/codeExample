delete from valueset r where r.resource ->> 'url' = 'ValueSet/Claim_result_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Claim_result_types';

select resource_create('{"resourceType": "ValueSet", "id": "Claim_result_types", "name": "Claim_result_types", "title": "Результат обращения пациента", "description": "Результат обращения пациента", "status": "active", "url": "ValueSet/Claim_result_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Death", "code": "Death", "system": "ValueSet/Claim_result_types", "display": "Смерть"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Transfer", "code": "Transfer", "system": "ValueSet/Claim_result_types", "display": "Перевод"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Leaving", "code": "Leaving", "system": "ValueSet/Claim_result_types", "display": "Самовольный уход"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Survey_refusal ", "code": "Survey_refusal ", "system": "ValueSet/Claim_result_types", "display": "Отказ от обследования"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Hospitalization", "code": "Hospitalization", "system": "ValueSet/Claim_result_types", "display": "Госпитализация"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Hospitalization_rejection", "code": "Hospitalization_rejection", "system": "ValueSet/Claim_result_types", "display": "Отказ в госпитализации"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Claim_result_types:Hospitalization_refusal ", "code": "Hospitalization_refusal ", "system": "ValueSet/Claim_result_types", "display": "Отказ пациента от госпитализации"}'::jsonb);