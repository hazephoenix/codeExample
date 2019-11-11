create sequence if not exists pk_seq
	as integer start with 1 increment by 1;

drop table if exists observation_duration_history cascade;

create table observation_duration_history
(
    id         bigint not null default nextval('pk_seq') constraint observation_duration_history_pkey primary key,
    fire_date  timestamp not null,
    code       text not null,
    diagnosis  text not null,
    severity   text not null,
    duration   integer not null
);

comment on table observation_duration_history is 'История продолжительности проведения услуг';
comment on column observation_duration_history.id is 'id';
comment on column observation_duration_history.fire_date is 'время добавления записи';
comment on column observation_duration_history.code is 'код услуги';
comment on column observation_duration_history.diagnosis is 'код диагноза';
comment on column observation_duration_history.severity is 'степень тяжести';
comment on column observation_duration_history.duration is 'продолжительность, в секундах';

alter table observation_duration_history owner to ${owner};