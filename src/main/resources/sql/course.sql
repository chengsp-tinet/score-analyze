-- auto-generated definition
create table course
(
    id          int auto_increment
        primary key,
    course_id   int                                null,
    course_name varchar(255)                       null,
    create_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    full_score  int                                null,
    constraint course_id_unique_index
        unique (course_id) comment '课程id唯一索引',
    constraint course_name_unique_index
        unique (course_name) comment '课程名称唯一索引'
)
    engine = MyISAM;

