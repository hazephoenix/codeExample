create sequence if not exists pk_seq
    as integer start with 1 increment by 1;

drop table if exists training_sample cascade;

create table training_sample
(
    id                       bigint not null default nextval('pk_seq')
        constraint training_sample_pkey primary key,
    systolic_bp              int,
    diastolic_bp             int,
    age                      int,
    gender                   text,
    weight                   int,
    height                   int,
    pulse_rate               int,
    heart_rate               int,
    breathing_rate           int,
    upper_respiratory_airway text,
    consciousness_assessment text,
    blood_oxygen_saturation  int,
    body_temperature         decimal,
    pain_intensity           int,
    patient_can_stand        text,
    complaints               text,
    severity                 text,
    diagnosis                text   not null
);

comment on table training_sample is 'Предполагаемый диагноз МКБ по набору признаков';
comment on column training_sample.id is 'id';
comment on column training_sample.systolic_bp is 'Систолическое артериальное давление';
comment on column training_sample.diastolic_bp is 'Диастолическое артериальное давление';
comment on column training_sample.age is 'Возраст';
comment on column training_sample.gender is 'Пол';
comment on column training_sample.weight is 'Вес';
comment on column training_sample.height is 'Рост';
comment on column training_sample.pulse_rate is 'Частота пульса';
comment on column training_sample.heart_rate is 'Частота сердечных сокращений';
comment on column training_sample.breathing_rate is 'Частота дыхания';
comment on column training_sample.upper_respiratory_airway is 'Верхние дыхательные пути';
comment on column training_sample.consciousness_assessment is 'Оценка уровня сознания';
comment on column training_sample.blood_oxygen_saturation is 'Уровень оксигенации крови';
comment on column training_sample.body_temperature is 'Температура тела';
comment on column training_sample.pain_intensity is 'Интенсивность боли';
comment on column training_sample.patient_can_stand is 'Пациент может стоять';
comment on column training_sample.complaints is 'Жалобы';
comment on column training_sample.severity is 'Степень тяжести';
comment on column training_sample.diagnosis is 'код диагноза';

alter table training_sample
    owner to ${owner};