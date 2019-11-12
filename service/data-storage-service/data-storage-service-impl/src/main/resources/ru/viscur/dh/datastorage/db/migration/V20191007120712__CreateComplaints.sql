delete from valueset r where r.resource ->> 'url' = 'ValueSet/Complaints';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Complaints';

select resource_create('{"resourceType": "ValueSet", "id": "Complaints", "description": "Жалобы", "name": "Complaints", "title": "Коды жалоб", "status": "active", "url": "ValueSet/Complaints"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Complaints:Violent_pain", "code": "Violent_pain", "system": "ValueSet/Complaints", "display": "Острая боль",
  "alternatives": ["острая боль", "резкая боль", "сильная боль", "острые боли", "резкие боли", "сильные боли"]}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Complaints:Fever", "code": "Fever", "system": "ValueSet/Complaints", "display": "Лихорадка",
  "alternatives": ["лихорадка", "лихорадит"]}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Complaints:Debility", "code": "Debility", "system": "ValueSet/Complaints", "display": "Слабость",
  "alternatives": ["слабость", "недомогаение", "усталость"]}'::jsonb);