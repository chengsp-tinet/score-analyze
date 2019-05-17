-- auto-generated definition
create table exam_group
(
    id              int auto_increment
        primary key,
    exam_group_id   int                                null,
    exam_group_name varchar(255)                       null,
    create_time     datetime default CURRENT_TIMESTAMP null,
    constraint exam_group_id_unique_index
        unique (exam_group_id),
    constraint exam_group_name_unique_index
        unique (exam_group_name)
);

