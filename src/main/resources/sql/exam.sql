-- auto-generated definition
create table exam
(
    id              int auto_increment
        primary key,
    course_id       varchar(255)                       null,
    course_name     varchar(255)                       null,
    create_time     datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    end_time        datetime                           null,
    exam_id         int                                null,
    exam_name       varchar(255)                       null,
    start_time      datetime                           null,
    student_count   int                                null,
    exam_group_id   int                                null,
    exam_group_name varchar(255)                       null,
    constraint exam_id_unique_index
        unique (exam_id),
    constraint exam_name_unique_index
        unique (exam_name)
)
    engine = MyISAM;

