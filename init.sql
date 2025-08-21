create table if not exists task_entity
(
    create_at  datetime(6)  null,
    execute_at datetime(6)  null,
    id         bigint auto_increment
    primary key,
    update_at  datetime(6)  null,
    status     varchar(255) null,
    task_id    varchar(255) null,
    payload    json         null,
    constraint idx_task_id
    unique (task_id)
    );
