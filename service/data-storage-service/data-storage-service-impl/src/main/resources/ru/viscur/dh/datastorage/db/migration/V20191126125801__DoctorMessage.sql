drop table if exists doctor_message cascade;

create table doctor_message
(
    id                     varchar(36) primary key,
    date_time              timestamp not null,
    clinical_impression_id text      not null,
    message_type           varchar(50),
    hidden                 boolean   not null default false
);

comment on table doctor_message is 'Сообщения для врача';
comment on column doctor_message.id is 'ID сообщения (GUID)';
comment on column doctor_message.date_time is 'Дата и время сообщения';
comment on column doctor_message.clinical_impression_id is 'ID обследования';
comment on column doctor_message.message_type is 'ID обследования';

