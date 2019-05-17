-- auto-generated definition
create table admin
(
    id          int auto_increment
        primary key,
    admin_id    int                                null,
    admin_name  varchar(255)                       null,
    create_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    password    varchar(255)                       null
)
    engine = MyISAM;

