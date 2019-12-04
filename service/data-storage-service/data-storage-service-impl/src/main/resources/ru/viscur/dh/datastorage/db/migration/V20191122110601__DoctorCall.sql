drop table if exists doctor_call cascade;

create table doctor_call
(
    id               varchar(36) primary key,
    date_time        timestamp   not null,
    caller_id        text        not null,
    specialization_category    varchar(20) not null,
    doctor_id        text        not null,
    goal             varchar(20) not null,
    patient_severity varchar(6)  not null,
    location_id      text        not null,
    comment          text,
    status           varchar(8)  not null,
    time_to_arrival  smallint
);

comment on table doctor_call is 'Вызовы врачей';
comment on column doctor_call.id is 'ID вызова (GUID)';
comment on column doctor_call.date_time is 'Дата и время вызова';
comment on column doctor_call.caller_id is 'ID врача который создал вызов. Ссылка на practitioner.id';
comment on column doctor_call.specialization_category is 'Категория специализации которую выбрали при вызове.';
comment on column doctor_call.doctor_id is 'ID врача для которого создан вызов. Ссылка на practitioner.id';
comment on column doctor_call.goal is 'Цель вызова';
comment on column doctor_call.patient_severity is 'Степень тяжести пациента';
comment on column doctor_call.location_id is 'ID кабинета в который необходимо придти вызванному врачу';
comment on column doctor_call.comment is 'Комментарий';
comment on column doctor_call.status is 'Статус вызова';
comment on column doctor_call.time_to_arrival is 'Время до прибытия врача. (минуты. 5, 10, 15)';

