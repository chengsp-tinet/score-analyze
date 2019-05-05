
-- auto-generated definition
create table student
(
    id             int auto_increment
        primary key,
    class_id       int                                null,
    create_time    datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    student_id     bigint                             null,
    student_name   varchar(255)                       null,
    to_school_year int                                null,
    type           int                                null,
    constraint student_id_unique_index
        unique (student_id)
)
    engine = MyISAM;

