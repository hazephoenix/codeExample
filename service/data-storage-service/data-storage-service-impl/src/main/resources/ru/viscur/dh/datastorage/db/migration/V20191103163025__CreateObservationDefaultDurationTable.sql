create sequence if not exists pk_seq
	as integer start with 1 increment by 1;

drop table if exists observation_default_duration cascade;

create table observation_default_duration
(
    id         bigint not null default nextval('pk_seq') constraint observation_default_duration_pkey primary key,
    code       text not null,
    severity   text not null,
    duration   integer not null
);

comment on table observation_default_duration is 'Продолжительности проведения услуг по умолчанию';
comment on column observation_default_duration.id is 'id';
comment on column observation_default_duration.code is 'код услуги';
comment on column observation_default_duration.severity is 'степень тяжести';
comment on column observation_default_duration.duration is 'продолжительность, в секундах';

alter table observation_default_duration owner to ${owner};