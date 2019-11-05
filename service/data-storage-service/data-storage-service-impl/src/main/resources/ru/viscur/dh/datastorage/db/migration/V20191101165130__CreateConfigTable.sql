create sequence if not exists pk_seq
	as integer start with 1 increment by 1;

drop table if exists config cascade;

create table config
(
    id    bigint not null default nextval('pk_seq') constraint config_pkey primary key,
    code  text not null,
    value text not null
);

comment on table config is 'Настройки системы';
comment on column config.id is 'id';
comment on column config.code is 'Код';
comment on column config.value is 'Значение';

alter table config owner to ${owner};