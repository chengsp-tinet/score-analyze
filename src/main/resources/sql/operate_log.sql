
-- auto-generated definition
create table operate_log
(
    id            int auto_increment
        primary key,
    create_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    description   int                                null,
    interface_url varchar(255)                       null,
    method        varchar(255)                       null,
    param         varchar(255)                       null,
    status        int                                null,
    username      varchar(255)                       null
)
    engine = MyISAM;

