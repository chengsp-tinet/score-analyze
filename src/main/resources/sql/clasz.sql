-- auto-generated definition
create table clasz
(
    id             int auto_increment
        primary key,
    class_id       int                                null,
    class_num      int                                null,
    create_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    to_school_year int                                null,
    type           int                                null,
    constraint class_id_unique_id
        unique (class_id)
)
    engine = MyISAM;

