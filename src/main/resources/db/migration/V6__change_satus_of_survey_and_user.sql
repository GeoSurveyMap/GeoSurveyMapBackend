alter table survey
    rename column is_accepted to status;

alter table survey
    alter column status type varchar(255);

alter table survey
    add constraint chk_status_enum
        check (status in ('PENDING', 'ACCEPTED', 'REJECTED'));

alter table app_user
    add column status varchar(255);

alter table app_user
    add constraint chk_user_status_enum
        check (status in ('ACTIVE', 'BANNED'));