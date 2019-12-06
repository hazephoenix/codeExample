drop index if exists pcall_practitioner cascade;
drop index if exists pcall_caller cascade;
drop table if exists practitioner_call_awaiting_ref cascade;
drop table if exists doctor_call cascade;
drop table if exists practitioner_call cascade;

create table practitioner_call
(
    id                      varchar(36) primary key,
    date_time               timestamp   not null,
    caller_id               text        not null,
    specialization_category varchar(20) not null,
    practitioner_id         text        not null,
    goal                    varchar(20) not null,
    patient_severity        varchar(6)  not null,
    location_id             text        not null,
    comment                 text,
    status                  varchar(8)  not null,
    time_to_arrival         smallint
);


create index pcall_practitioner on practitioner_call (practitioner_id);
create index pcall_caller on practitioner_call (caller_id);

comment on table practitioner_call is 'Вызовы врачей';
comment on column practitioner_call.id is 'ID вызова (GUID)';
comment on column practitioner_call.date_time is 'Дата и время вызова';
comment on column practitioner_call.caller_id is 'ID врача который создал вызов. Ссылка на practitioner.id';
comment on column practitioner_call.specialization_category is 'Категория специализации которую выбрали при вызове.';
comment on column practitioner_call.practitioner_id is 'ID врача для которого создан вызов. Ссылка на practitioner.id';
comment on column practitioner_call.goal is 'Цель вызова';
comment on column practitioner_call.patient_severity is 'Степень тяжести пациента';
comment on column practitioner_call.location_id is 'ID кабинета в который необходимо придти вызванному врачу';
comment on column practitioner_call.comment is 'Комментарий';
comment on column practitioner_call.status is 'Статус вызова';
comment on column practitioner_call.time_to_arrival is 'Время до прибытия врача. (минуты. 5, 10, 15)';


create table practitioner_call_awaiting_ref
(
    call_id          varchar(36) primary key,
    stage_date_time  timestamp   not null,
    stage            varchar(20) not null,
    voice_call_count smallint,
    constraint call_fk foreign key (call_id)
        references practitioner_call (id) on delete cascade
);

comment on table practitioner_call_awaiting_ref is 'Ссылки на ожидающие вызовы врачей';
comment on column practitioner_call_awaiting_ref.call_id is 'ID вызова (по совместительству PK)';
comment on column practitioner_call_awaiting_ref.stage_date_time is 'Дата и время когда перешли на стадию';
comment on column practitioner_call_awaiting_ref.stage is 'Стадия ожидания';
comment on column practitioner_call_awaiting_ref.voice_call_count is 'Сколько раз вызвали голосом';


