drop table if exists user;

create table user(
                     id bigint primary key auto_increment comment '主键',
                     username varchar(256) comment '用户昵称',
                     user_account varchar(256) comment '用户账号',
                     avatar varchar(1024) comment '头像url',
                     gender tinyint comment '性别',
                     password varchar(512) comment '密码',
                     phone varchar(128) comment '电话',
                     email varchar(256) comment '邮箱',
                     status tinyint default 0 comment '用户状态',
                     create_time datetime comment '创建时间',
                     update_time datetime comment '更新时间',
                     is_delete tinyint default 0 comment '数据是否删除(逻辑删除, 非直接删除)'
)