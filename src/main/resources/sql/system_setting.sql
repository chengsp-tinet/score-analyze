
-- auto-generated definition
create table system_setting
(
    id          int auto_increment
        primary key,
    create_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    name        varchar(255)                       null,
    remark      varchar(255)                       null,
    value       varchar(255)                       null
)
    engine = MyISAM;

