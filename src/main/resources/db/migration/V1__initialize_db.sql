CREATE EXTENSION IF NOT EXISTS postgis;

create table location
(
    id          bigint not null,
    location    geometry(Point,4326),
    created_at  timestamp,
    created_by  varchar(255),
    modified_at timestamp,
    modified_by varchar(255),
    primary key (id)
);

create sequence location_seq start with 1 increment by 1;