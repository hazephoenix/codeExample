delete from valueset r where r.resource ->> 'url' = 'ValueSet/Practitioner_qualifications';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Practitioner_qualifications';

select fhirbase_create('{"resourceType": "ValueSet", "id": "Practitioner_qualifications", "description": "Специальности мед. работников", "name": "Practitioner_qualifications", "title": "Специальности мед. работников", "status": "active", "url": "ValueSet/Practitioner_qualifications"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Obstetrician", "code": "Obstetrician", "system": "ValueSet/Practitioner_qualifications", "display": "Акушер"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Obstetrician-gynecologist", "code": "Obstetrician-gynecologist", "system": "ValueSet/Practitioner_qualifications", "display": "Акушер-гинеколог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Venereologist", "code": "Venereologist", "system": "ValueSet/Practitioner_qualifications", "display": "Венеролог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Doctor", "code": "Doctor", "system": "ValueSet/Practitioner_qualifications", "display": "Врач"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Anesthetist", "code": "Anesthetist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-анестезиолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Gynecologist", "code": "Gynecologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-гинеколог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Neurologist", "code": "Neurologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-невролог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Pediatrician", "code": "Pediatrician", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-педиатр"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Therapist_in_the_ward", "code": "Therapist_in_the_ward", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-терапевт в приемном отделении"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Therapist_in_the_hospital", "code": "Therapist_in_the_hospital", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-терапевт в стационаре"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Urologist", "code": "Urologist", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-уролог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Surgeon_-_senior_team_doctor_in_the_ward", "code": "Surgeon_-_senior_team_doctor_in_the_ward", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-хирург - старший врач бригады в приемном отделении"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Surgeon_in_the_ward", "code": "Surgeon_in_the_ward", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-хирург в приемном отделении"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Hospital_surgeon", "code": "Hospital_surgeon", "system": "ValueSet/Practitioner_qualifications", "display": "Врач-хирург в стационаре"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Cloakroom", "code": "Cloakroom", "system": "ValueSet/Practitioner_qualifications", "display": "Гардеробщик"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Geneticist", "code": "Geneticist", "system": "ValueSet/Practitioner_qualifications", "display": "Генетик"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Gynecologist", "code": "Gynecologist", "system": "ValueSet/Practitioner_qualifications", "display": "Гинеколог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Beautician", "code": "Beautician", "system": "ValueSet/Practitioner_qualifications", "display": "Косметолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Nurse", "code": "Nurse", "system": "ValueSet/Practitioner_qualifications", "display": "Медицинская сестра"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Dressing_nurse", "code": "Dressing_nurse", "system": "ValueSet/Practitioner_qualifications", "display": "Медицинская сестра перевязочной"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Receptionist_nurse", "code": "Receptionist_nurse", "system": "ValueSet/Practitioner_qualifications", "display": "Медицинская сестра приемного отделения"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Treatment_Room_Nurse", "code": "Treatment_Room_Nurse", "system": "ValueSet/Practitioner_qualifications", "display": "Медицинская сестра процедурного кабинета"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Cosmetology_nurse", "code": "Cosmetology_nurse", "system": "ValueSet/Practitioner_qualifications", "display": "Медсестра в косметологии"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Expert_in_narcology", "code": "Expert_in_narcology", "system": "ValueSet/Practitioner_qualifications", "display": "Нарколог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Neurosurgeon", "code": "Neurosurgeon", "system": "ValueSet/Practitioner_qualifications", "display": "Нейрохирург"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Neonatologist", "code": "Neonatologist", "system": "ValueSet/Practitioner_qualifications", "display": "Неонатолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Optometrist", "code": "Optometrist", "system": "ValueSet/Practitioner_qualifications", "display": "Оптометрист"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Otolaryngologist", "code": "Otolaryngologist", "system": "ValueSet/Practitioner_qualifications", "display": "Отоларинголог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Pathologist", "code": "Pathologist", "system": "ValueSet/Practitioner_qualifications", "display": "Патологоанатом"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Plastic_surgeon", "code": "Plastic_surgeon", "system": "ValueSet/Practitioner_qualifications", "display": "Пластический хирург"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Pharmacist", "code": "Pharmacist", "system": "ValueSet/Practitioner_qualifications", "display": "Провизор"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Psychiatrist", "code": "Psychiatrist", "system": "ValueSet/Practitioner_qualifications", "display": "Психиатр"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Psychotherapist", "code": "Psychotherapist", "system": "ValueSet/Practitioner_qualifications", "display": "Психотерапевт"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Rehabilitologist", "code": "Rehabilitologist", "system": "ValueSet/Practitioner_qualifications", "display": "Реабилитолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Radiologist", "code": "Radiologist", "system": "ValueSet/Practitioner_qualifications", "display": "Рентгенолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Hospital_attendant", "code": "Hospital_attendant", "system": "ValueSet/Practitioner_qualifications", "display": "Санитар транспортировочной бригады"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Medical_assistant", "code": "Medical_assistant", "system": "ValueSet/Practitioner_qualifications", "display": "Санитарка"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Dentist", "code": "Dentist", "system": "ValueSet/Practitioner_qualifications", "display": "Стоматолог"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Dental_Hygienist", "code": "Dental_Hygienist", "system": "ValueSet/Practitioner_qualifications", "display": "Стоматолог-гигиенист"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Medical_examiner", "code": "Medical_examiner", "system": "ValueSet/Practitioner_qualifications", "display": "Судмедэксперт"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Therapist", "code": "Therapist", "system": "ValueSet/Practitioner_qualifications", "display": "Терапевт"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Pharmacist", "code": "Pharmacist", "system": "ValueSet/Practitioner_qualifications", "display": "Фармацевт"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Paramedic", "code": "Paramedic", "system": "ValueSet/Practitioner_qualifications", "display": "Фельдшер"}'::jsonb);
select fhirbase_create('{"resourceType": "Concept", "id": "Practitioner_qualifications:Surgeon", "code": "Surgeon", "system": "ValueSet/Practitioner_qualifications", "display": "Хирург"}'::jsonb);