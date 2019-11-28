delete from observation_default_duration;

insert into observation_default_duration (code, severity, duration) values ('Clinical_impression', 'RED', 1800);
insert into observation_default_duration (code, severity, duration) values ('Clinical_impression', 'YELLOW', 3600);
insert into observation_default_duration (code, severity, duration) values ('Clinical_impression', 'GREEN', 5400);