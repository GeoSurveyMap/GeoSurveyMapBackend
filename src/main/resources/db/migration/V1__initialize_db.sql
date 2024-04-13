CREATE EXTENSION IF NOT EXISTS postgis;

create table location
(
    location    geometry(Point,4326),
    survey_id   bigint not null,
    created_at  timestamp,
    created_by  varchar(255),
    modified_at timestamp,
    modified_by varchar(255),
    primary key (survey_id)
);

create sequence location_seq start with 1 increment by 1;

create table survey
(
    id          bigint not null,
    created_at  timestamp,
    created_by  varchar(255),
    modified_at timestamp,
    modified_by varchar(255),
    category    varchar(255) check (category in ('DRY_SOILS', 'WET_SOILS', 'EROSION', 'SEALED_SOILS', 'DEGRADATION',
                                                 'LOSS_OF_ORGANIC_MATTER', 'PH', 'BIODIVERSITY')),
    description varchar(255),
    solution    varchar(255),
    primary key (id)
);

create sequence survey_seq start with 1 increment by 1;
alter table if exists location add constraint FKpwten3sr3klggvgaavacxljij foreign key (survey_id) references survey;