alter table survey
    add column file_path varchar;

alter table survey
    add column is_photo_accepted boolean default false;