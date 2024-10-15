delete from location where survey_id > 0;

delete from survey where id > 0;

create table app_user
(
    id          bigint not null,
    created_at  timestamp(6),
    created_by  varchar(255),
    modified_at timestamp(6),
    modified_by varchar(255),
    email       varchar(255),
    kinde_id    varchar(255),
    role        varchar(255) check (role in ('ROLE_ADMIN', 'ROLE_USER', 'ROLE_PRO')),
    primary key (id)
);

alter table if exists app_user
    add constraint UK_bv6wtxeo62gwn93mtysf1fv4q unique (kinde_id);

create sequence app_user_seq start with 1 increment by 1;

alter table survey
    add column user_id bigint;

alter table if exists survey
    add constraint FKt4ecp5rbcwornp3raejftpx1a foreign key (user_id) references app_user;