drop table if exists reader_event_log cascade;
create table reader_event_log
(
    id      bigint not null primary key,
    stamp   timestamp with time zone,
    reader  text   not null,
    channel text   not null,
    zone    text   not null,
    tags    text   not null
);

comment on table reader_event_log is 'Зона для отслеживания местоположение сотрудников';
comment on column reader_event_log.id is 'идентификатор';
comment on column reader_event_log.stamp is 'время генерации события';
comment on column reader_event_log.reader is 'считыватель';
comment on column reader_event_log.channel is 'канал (антена)';
comment on column reader_event_log.zone is 'идентификатор зоны';
comment on column reader_event_log.tags is 'список тегов через запятую';

alter table reader_event_log
    owner to ${owner};
