drop table if exists tag cascade;
create table tag
(
    tag_id          text not null primary key,
    practitioner_id text not null
);

comment on table tag is 'Соответствие кодов rfid-меток и сотрудников';
comment on column tag.tag_id is 'идентификатор rfid-метки';
comment on column tag.practitioner_id is 'идентификатор сотрудника';

alter table tag owner to ${owner};
