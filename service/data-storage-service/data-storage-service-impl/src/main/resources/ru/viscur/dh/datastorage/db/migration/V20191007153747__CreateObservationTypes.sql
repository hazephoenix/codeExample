delete from valueset r where r.resource ->> 'url' = 'ValueSet/Observation_types';
delete from concept r where r.resource ->> 'system' = 'ValueSet/Observation_types';

select resource_create('{"resourceType": "ValueSet", "id": "Observation_types", "url": "ValueSet/Observation_types", "description": "Типы процедур/услуг", "name": "Observation_types", "title": "Типы процедур/услуг", "status": "active"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Vital_signs", "code": "Vital_signs", "parentCode": null, "display": "Жизненно важные физиологические показатели", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_upper_level", "code": "Blood_pressure_upper_level", "parentCode": "Vital_signs", "display": "Артериальное давление верхняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_pressure_lower_level", "code": "Blood_pressure_lower_level", "parentCode": "Vital_signs", "display": "Артериальное давление нижняя граница", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Heart_rate", "code": "Heart_rate", "display": "Частота сердечных сокращений", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Pulse_rate", "code": "Pulse_rate", "display": "Частота пульса", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Breathing_rate", "code": "Breathing_rate", "display": "Частота дыхания", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Body_temperature", "code": "Body_temperature", "display": "Температура тела", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Electrocardiogram", "code": "Electrocardiogram", "display": "Электрокардиограмма", "parentCode": "Vital_signs", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Blood_oxygen_saturation", "code": "Blood_oxygen_saturation", "parentCode": "Vital_signs", "display": "Степень насыщения крови кислородом", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Weight", "code": "Weight", "parentCode": "Vital_signs", "display": "Вес", "system": "ValueSet/Observation_types"}'::jsonb);
select resource_create('{"resourceType": "Concept", "id": "Observation_types:Height", "code": "Height", "parentCode": "Vital_signs", "display": "Рост", "system": "ValueSet/Observation_types"}'::jsonb);


select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "Inspection", "display": "Осмотры", "id": "Observation_types:Inspection", "priority": "0.1"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "Ultrasound", "display": "УЗИ", "id": "Observation_types:Ultrasound"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "Blood_analysis", "display": "Исследования крови", "id": "Observation_types:Blood_analysis", "priority": "0.8"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "Urine_analysis", "display": "Исследование мочи", "id": "Observation_types:Urine_analysis", "priority": "0.9"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "ECG", "display": "ЭКГ", "id": "Observation_types:ECG"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "Endoscopy", "display": "Эндоскопия", "id": "Observation_types:Endoscopy", "priority": "0.2"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": null, "code": "X-ray", "display": "Рентген", "id": "Observation_types:X-ray"}'::jsonb);

select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "Clinical_impression", "display": "Обращение в скорую помощь", "id": "Observation_types:Clinical_impression"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "Inspection_on_reception", "display": "Первичный осмотр при регистрации обращения", "id": "Observation_types:Inspection_on_reception"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтФтизиатр", "display": "Осмотр (консультация) врача фтизиатра в стационаре", "id": "Observation_types:СтФтизиатр.Inspection_of_a_TB_doctor"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтГинек", "display": "Осмотр (консультация) врача-акушера-гинеколога в стационаре", "id": "Observation_types:СтГинек.Inspection_of_a_gynaecologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтДермат", "display": "Осмотр (консультация) врача-дерматовенеролога в стационаре", "id": "Observation_types:СтДермат.Inspection_of_a_dermatovenerologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтИнф", "display": "Осмотр (консультация) врача-инфекциониста в стационаре", "id": "Observation_types:СтИнф.Inspection_of_an_infectious_disease_specialist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтКардиол", "display": "Осмотр (консультация) врача-кардиолога в стационаре", "id": "Observation_types:СтКардиол.Inspection_of_a_cardiologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "ST_KLIN_FARM", "display": "Осмотр (консультация) врача-клинического фармаколога в стационаре", "id": "Observation_types:ST_KLIN_FARM.Inspection_of_a_doctor-clinical_pharmacologist"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтНев", "display": "Осмотр (консультация) врача-невролога в стационаре", "id": "Observation_types:СтНев.Inspection_of_a_neurologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтОнк", "display": "Осмотр (консультация) врача-онколога в стационаре", "id": "Observation_types:СтОнк.Inspection_of_an_oncologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СТлор", "display": "Осмотр (консультация) врача-отоларинголога в стационаре", "id": "Observation_types:СТлор.Inspection_of_an_otolaryngologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтОфт", "display": "Осмотр (консультация) врача-офтальмолога в стационаре", "id": "Observation_types:СтОфт.Inspection_of_an_ophthalmologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтПс", "display": "Осмотр (консультация) врача-психиатра в стационаре", "id": "Observation_types:СтПс.Inspection_of_a_psychiatrist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтРевмат", "display": "Осмотр (консультация) врача-ревматолога в стационаре", "id": "Observation_types:СтРевмат.Inspection_of_a_rheumatologist"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтТер", "display": "Осмотр (консультация) врача-терапевта в стационаре", "id": "Observation_types:СтТер.Inspection_of_a_therapeutist"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтУрол", "display": "Осмотр (консультация) врача-уролога в стационаре", "id": "Observation_types:СтУрол.Inspection_of_a_urologist"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтХир", "display": "Осмотр (консультация) врача-хирурга в стационаре", "id": "Observation_types:СтХир.Inspection_of_a_surgeon"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "СтЭндокр", "display": "Осмотр (консультация) врача-эндокринолога в стационаре", "id": "Observation_types:СтЭндокр.Inspection_of_the_endocrinologist"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "B01.020.001", "display": "Прием (осмотр, консультация) врача по лечебной физкультуре", "id": "Observation_types:B01.020.001.Reception_(examination,_consultation)_of_a_physician_in_physiotherapy_exercises"}'::jsonb);
-- select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Inspection", "code": "B01.054.01", "display": "Прием (осмотр, консультация) врача-физиотерапевта первичный", "id": "Observation_types:B01.054.01.Primary_reception_(examination,_consultation)_of_a_physiotherapist"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.28.002.003", "display": "Ультразвуковое исследование мочевого пузыря", "id": "Observation_types:A04.28.002.003.Ultrasound_of_bladder"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.16.001", "display": "Ультразвуковое исследование органов брюшной полости (комплексное)", "id": "Observation_types:A04.16.001.Ultrasound_of_the_abdominal_organs_(complex)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.16.001.001", "display": "Ультразвуковое исследование органов брюшной полости (обзорное)", "id": "Observation_types:A04.16.001.001.Ultrasound_of_the_abdominal_organs_(overview)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.020.001.001", "display": "Ультразвуковое исследование органов малого таза", "id": "Observation_types:A04.020.001.001.Ultrasound_of_the_pelvic_organs"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.09.001", "display": "Ультразвуковое исследование плевральной полости", "id": "Observation_types:A04.09.001.Ultrasound_of_the_pleural_cavity"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Ultrasound", "code": "A04.28.002.001", "display": "Ультразвуковое исследование почек", "id": "Observation_types:A04.28.002.001.Ultrasound_of_kidneys"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "B03.016.004ГМУ_СП", "display": "Анализ крови биохимический общетерапевтический (СибГМУ_скорая)", "id": "Observation_types:B03.016.004ГМУ_СП.Biochemical_blood_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Urine_analysis", "code": "B03.016.006ГМУ_СП", "display": "Анализ мочи общий (СибГМУ_скорая)", "id": "Observation_types:B03.016.006ГМУ_СП.General_urine_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "A09.05.036.001ГМУ_СП", "display": "Анализ на уровень алкоголя в крови", "id": "Observation_types:A09.05.036.001ГМУ_СП.Blood_alcohol_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Urine_analysis", "code": "A09.28.029ГМУ_СП", "display": "Исследование мочи на хорионический гонадотропин (СибГМУ_скорая)", "id": "Observation_types:A09.28.029ГМУ_СП.Urinalysis_for_chorionic_gonadotropin"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "A09.20.003ГМУ_СП", "display": "Исследование показателя системы свертывания крови Д-димер", "id": "Observation_types:A09.20.003ГМУ_СП.Blood_coagulation_system_D-dimer_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "A09.05.049.01ГМУ_СП", "display": "Исследование уровня факторов свертывания в крови (РФМК) (СибГМУ_скорая)", "id": "Observation_types:A09.05.049.01ГМУ_СП.Coagulation_factors_level_in_the_blood_analysis_(RFMK)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "B03.016.002ГМУ_СП", "display": "Общий (клинический) анализ крови (СибГМУ_скорая)", "id": "Observation_types:B03.016.002ГМУ_СП.General_blood_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Blood_analysis", "code": "B03.016.004ГМУ_СП_ТК", "display": "Тропониновый тест и КФК МВ (СибГМУ_скорая)", "id": "Observation_types:B03.016.004ГМУ_СП_ТК.Troponin_analysis_and_KFK_MV"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "ECG", "code": "A05.10.002", "display": "Проведение электрокардиографических исследований", "id": "Observation_types:A05.10.002.Electrocardiographic_analysis"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.19.001", "display": "Аноскопия", "id": "Observation_types:A03.19.001.Anoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A11.09.005", "display": "Бронхоскопический лаваж", "id": "Observation_types:A11.09.005.Bronchoscopic_lavage"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.09.001", "display": "Бронхоскопия", "id": "Observation_types:A03.09.001.Bronchoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.08.001.001", "display": "Видеоларингоскопия", "id": "Observation_types:A03.08.001.001.Video_laryngoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.18.001", "display": "Колоноскопия", "id": "Observation_types:A03.18.001.Colonoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.008.001", "display": "Ларингоскопия", "id": "Observation_types:A03.008.001.Laryngoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.07.007", "display": "Определение степени открывания рта и ограничения подвижности нижней челюсти", "id": "Observation_types:A01.07.007.Determining_the_degree_of_opening_the_mouth_and_limiting_the_mobility_of_the_lower_jaw"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.19.003", "display": "Пальпация при патологии сигмовидной и прямой кишки", "id": "Observation_types:A01.19.003.Palpation_in_pathology_of_the_sigmoid_and_rectum"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.19.002", "display": "Ректороманоскопия", "id": "Observation_types:A03.19.002.Sigmoidoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A06.14.007", "display": "Ретроградная холангиопанкреатография (РХПГ)", "id": "Observation_types:A06.14.007.Retrograde_cholangiopancreatography_(RCHP)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.09.001", "display": "Сбор анамнеза и жалоб при заболеваниях легких и бронхов", "id": "Observation_types:A01.09.001.Collection_of_anamnesis_and_complaints_in_diseases_of_the_lungs_and_bronchi"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.14.001", "display": "Сбор анамнеза и жалоб при заболеваниях печени и желчевыводящих путей", "id": "Observation_types:A01.14.001.Collection_of_anamnesis_and_complaints_in_diseases_of_the_liver_and_biliary_tract"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.16.001", "display": "Сбор анамнеза и жалоб при заболеваниях пищевода, желудка, двенадцатиперстной кишки", "id": "Observation_types:A01.16.001.Collection_of_anamnesis_and_complaints_in_diseases_of_the_esophagus,_stomach,_duodenum"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.18.001", "display": "Сбор анамнеза и жалоб при заболеваниях толстой кишки", "id": "Observation_types:A01.18.001.Collection_of_anamnesis_and_complaints_in_diseases_of_the_colon"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.19.001", "display": "Сбор анамнеза и жалоб при патологии сигмовидной и прямой кишки", "id": "Observation_types:A01.19.001.Collection_of_anamnesis_and_complaints_with_pathology_of_the_sigmoid_and_rectum"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.19.003", "display": "Сигмоидоскопия", "id": "Observation_types:A03.19.003.Sigmoidoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.18.001.001", "display": "Толстокишечная видеоэндоскопия", "id": "Observation_types:A03.18.001.001.Colonic_video_endoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A01.19.004", "display": "Трансректальное пальцевое исследование", "id": "Observation_types:A01.19.004.Transrectal_finger_examination"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.09.002", "display": "Трахеоскопия", "id": "Observation_types:A03.09.002.Tracheoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.30.006.006", "display": "Узкоспектральное NBI-исследование органов желудочно-кишечного тракта", "id": "Observation_types:A03.30.006.006.Narrow-spectrum_NBI_examination_of_the_gastrointestinal_tract"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.30.006.001", "display": "Узкоспектральное эндоскопическое исследование гортани, трахеи и бронхов", "id": "Observation_types:A03.30.006.001.Narrow-spectral_endoscopic_examination_of_the_larynx,_trachea_and_bronchi"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.14.002", "display": "Холедохоскопия", "id": "Observation_types:A03.14.002.Choledochoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.30.007", "display": "Хромоскопия, контрастное исследование органов желудочно-кишечного тракта", "id": "Observation_types:A03.30.007.Chromoscopy,_a_contrast_study_of_the_gastrointestinal_tract"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.16.001", "display": "Эзофагогастродуоденоскопия", "id": "Observation_types:A03.16.001.Esophagogastroduodenoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A11.09.009", "display": "Эндобронхиальное введение лекарственных препаратов при бронхоскопии", "id": "Observation_types:A11.09.009.Endobronchial_administration_of_drugs_with_bronchoscopy"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A03.30.006", "display": "Эндоскопическое исследование внутренних органов", "id": "Observation_types:A03.30.006.Endoscopic_examination_of_internal_organs"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "Endoscopy", "code": "A11.09.006", "display": "Эндотрахеальное введение лекарственных препаратов", "id": "Observation_types:A11.09.006.Endotracheal_Administration"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.28.002", "display": "Внутривенная урография", "id": "Observation_types:A06.28.002.Intravenous_urography"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.28.013", "display": "Обзорная урография (рентгенография мочевыделительной системы)", "id": "Observation_types:A06.28.013.Survey_urography_(radiography_of_the_urinary_system)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.30.004", "display": "Обзорный снимок брюшной полости и органов малого таза", "id": "Observation_types:A06.30.004.Overview_picture_of_the_abdominal_cavity_and_pelvic_organs"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.03.005", "display": "Рентгенография всего черепа, в одной или более проекциях", "id": "Observation_types:A06.03.005.X-ray_of_the_entire_skull,_in_one_or_more_projections"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.31.011", "display": "Рентгенография органов грудной клетки в двух проекциях", "id": "Observation_types:A06.31.011.Chest_x-ray_in_two_projections"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.08.003", "display": "Рентгенография придаточных пазух носа", "id": "Observation_types:A06.08.003.Radiography_of_the_sinuses"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.03.023", "display": "Рентгенография ребра(ер)", "id": "Observation_types:A06.03.023.X-ray_of_the_rib(s)"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.18.002", "display": "Рентгеноконтроль прохождения контраста по толстому кишечнику", "id": "Observation_types:A06.18.002.X-ray_control_of_the_passage_of_contrast_through_the_large_intestine"}'::jsonb);
select resource_create('{"resourceType": "Concept", "system": "ValueSet/Observation_types", "parentCode": "X-ray", "code": "A06.28.007.01", "display": "Ретроградная урография", "id": "Observation_types:A06.28.007.01.Retrograde_urography"}'::jsonb);
