
-- auto-generated definition
create table score
(
    id              int auto_increment
        primary key,
    class_id        int                                        null,
    create_time     datetime         default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    exam_id         int                                        null,
    exam_name       varchar(255)                               null,
    grade_num       int                                        null,
    score           int(11) unsigned default 0                 null,
    student_id      int                                        null,
    student_name    varchar(255)                               null,
    course_id       int                                        null,
    course_name     varchar(255)                               null,
    exam_group_id   int                                        null,
    exam_group_name varchar(255)                               null,
    constraint exam_id_student_id_unique_index
        unique (exam_id, student_id)
)
    engine = MyISAM;

create index class_id_index
    on score (class_id);

create index course_id_index
    on score (course_id);

create index score_index
    on score (score);

