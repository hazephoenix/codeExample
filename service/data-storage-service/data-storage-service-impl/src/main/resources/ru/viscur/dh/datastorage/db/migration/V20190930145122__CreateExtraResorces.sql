drop table if exists QueueHistoryOfOffice;
create table QueueHistoryOfOffice
(
	id text not null
		constraint QueueHistoryOfOffice_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueHistoryOfOffice'::text,
	status resource_status not null,
	resource jsonb not null
);
alter table QueueHistoryOfOffice owner to ${owner};

drop table if exists QueueHistoryOfOffice_history;
create table QueueHistoryOfOffice_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueHistoryOfOffice'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint QueueHistoryOfOffice_history_pkey
		primary key (id, txid)
);
alter table QueueHistoryOfOffice_history owner to ${owner};

drop table if exists QueueHistoryOfPatient;
create table QueueHistoryOfPatient
(
	id text not null
		constraint QueueHistoryOfPatient_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueHistoryOfPatient'::text,
	status resource_status not null,
	resource jsonb not null
);
alter table QueueHistoryOfPatient owner to ${owner};

drop table if exists QueueHistoryOfPatient_history;
create table QueueHistoryOfPatient_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueHistoryOfPatient'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint QueueHistoryOfPatient_history_pkey
		primary key (id, txid)
);
alter table QueueHistoryOfPatient_history owner to ${owner};

drop table if exists QueueItem;
create table QueueItem
(
	id text not null
		constraint QueueItem_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueItem'::text,
	status resource_status not null,
	resource jsonb not null
);
alter table QueueItem owner to ${owner};

drop table if exists QueueItem_history;
create table QueueItem_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'QueueItem'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint QueueItem_history_pkey
		primary key (id, txid)
);
alter table QueueItem_history owner to ${owner};

drop table if exists CodeMap;
create table CodeMap
(
	id text not null
		constraint CodeMap_pkey
			primary key,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CodeMap'::text,
	status resource_status not null,
	resource jsonb not null
);
alter table CodeMap owner to ${owner};

drop table if exists CodeMap_history;
create table CodeMap_history
(
	id text not null,
	txid bigint not null,
	ts timestamp with time zone default CURRENT_TIMESTAMP,
	resource_type text default 'CodeMap'::text,
	status resource_status not null,
	resource jsonb not null,
	constraint CodeMap_history_pkey
		primary key (id, txid)
);
alter table CodeMap_history owner to ${owner};