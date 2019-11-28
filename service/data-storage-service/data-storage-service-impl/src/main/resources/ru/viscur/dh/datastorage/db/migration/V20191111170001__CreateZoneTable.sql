drop table if exists zone cascade;
create table zone
(
    zone_id    text not null primary key,
    name       text not null,
    office_ids text
);

comment on table zone is 'Зона для отслеживания местоположение сотрудников';
comment on column zone.zone_id is 'идентификатор зоны';
comment on column zone.name is 'наименование зоны';
comment on column zone.office_ids is 'перечисленные через запятую id помещений покрываемых зоной';

alter table zone
    owner to ${owner};
