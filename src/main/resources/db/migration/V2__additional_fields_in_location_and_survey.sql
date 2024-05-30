alter table survey
    add column affected_area double precision;

alter table location
    add column name varchar(255);