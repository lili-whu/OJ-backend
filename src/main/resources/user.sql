use liOJ;

drop table if exists user;
# user表
create table user
(
    id           bigint auto_increment comment '主键'
        primary key,
    username     varchar(256)      null comment '用户昵称',
    user_account varchar(256)      null comment '用户账号',
    avatar       varchar(1024)     null comment '头像url',
    gender       tinyint           null comment '性别',
    password     varchar(512)      null comment '密码',
    phone        varchar(128)      null comment '电话',
    email        varchar(256)      null comment '邮箱',
    status       tinyint default 0 not null comment '用户状态',
    create_time  datetime default current_timestamp not null comment '创建时间',
    update_time  datetime  default current_timestamp not null on update current_timestamp comment '更新时间',
    is_delete    tinyint default 0 not null comment '数据是否删除(逻辑删除, 非直接删除)',
    user_role    tinyint default 0 not null comment '用户角色, 0默认用户 1 管理员'
);

drop table if exists question;

create table question(
                         id bigint auto_increment primary key comment '主键',
                         title varchar(512) comment '标题',
                         description text comment '具体题目内容',
                         tags varchar(1024) comment '题目标签, json数组',
                         answer text comment '题解 todo 扩展为一个单独的数据表',
                         judge_case text comment '测试用例, json数组',
                         judge_config text comment '时空条件限制',
                         submit_num int default 0 not null comment '提交次数',
                         accept_num int default 0 not null comment '通过次数',
                         thumb_num int default 0 not null comment '点赞数',
                         favor_num int default 0 not null comment '收藏数',
                         create_id bigint not null comment '创建题目的用户id',
                         create_time  datetime default current_timestamp not null comment '创建时间',
                         update_time  datetime  default current_timestamp not null on update current_timestamp comment '更新时间',
                         is_delete    tinyint default 0 not null comment '逻辑删除',
                         index idx_create_id (create_id)
);

drop table if exists record_submit;

create table record_submit(
                              id bigint auto_increment primary key comment 'id',
                              language int comment '使用语言, 枚举值',
                              code text comment '代码',
                              judge_info text comment '判题信息(错误类型, 时间消耗, 空间消耗)',
                              status int default 0 not null comment '提交状态(0 未判题 1 判题中 2 成功 3 失败)',
                              question_id bigint not null comment '题目id',
                              create_id bigint comment '提交的用户id',
                              create_time  datetime default current_timestamp not null comment '创建时间',
                              update_time  datetime  default current_timestamp not null on update current_timestamp comment '更新时间',
                              is_delete tinyint default 0 not null comment '逻辑删除',
                              index idx_question_id (question_id),
                              index idx_create_id (create_id)
);