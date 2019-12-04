drop table if exists practitioner_message cascade;

create table practitioner_message
(
    id                     varchar(36) primary key,
    date_time              timestamp   not null,
    practitioner_id              text        not null,
    clinical_impression_id text        not null,
    message_type           varchar(50) not null,
    hidden                 boolean     not null default false
);

comment on table practitioner_message is 'Сообщения для врача';
comment on column practitioner_message.id is 'ID сообщения (GUID)';
comment on column practitioner_message.date_time is 'Дата и время сообщения';
comment on column practitioner_message.practitioner_id is 'ID врача';
comment on column practitioner_message.clinical_impression_id is 'ID обследования';
comment on column practitioner_message.message_type is 'Тип сообщения';
comment on column practitioner_message.hidden is 'Сообщение скрыто';

