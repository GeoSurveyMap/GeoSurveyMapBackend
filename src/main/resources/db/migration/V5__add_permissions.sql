alter table app_user
    add column permissions text;

alter table survey
    rename column is_photo_accepted to is_accepted;