# OJ系统后端
使用 java + springboot + mybatis_plus 构建的OJ系统
* 基础登录注册用户管理功能继承user-center-backend项目

## OJ主界面分析
时间限制, 内存限制 时间复杂度, 空间复杂度(只能使用特定包, 不能暴力解 => 安全性)
题目介绍, 输入, 输出, 输入用例, 输出用例

## 系统分析
后端编译代码, 运行代码, 输入测试用例, 获得结果, 比对测试结果

## 核心功能
1. 题目提交, 题目描述, 做题界面
2. 代码沙箱
   * 隔离的安全的环境, 不会影响系统运行
   * 资源分配, 限制用户的可执行内存
3. 判题规则
   * 提米用例的比对, 结果验证
4. 任务调度
   * 按照顺序排队完成判题
## 扩展思路:
1. 做题记录统计分析展示
2. 题解讨论, 点赞等论坛功能

## 2.6 题目模块开发
1. 题目和题目提交数据库表设计
   use liOJ;

drop table if exists user;
# user表
```mysql
drop table if exists question;

create table question(
id bigint auto_increment primary key comment '主键',
title varchar(512) comment '标题',
description text comment '具体题目内容',
tags varchar(1024) comment '题目标签, json数组(栈, 队列, 二叉树, 简单, 中等, 困难)',
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
index idx_question_id (question_id), # 创建题目索引方便后续查询某一题目的提交记录
index idx_create_id (create_id)
);
```
其中, judge_case judge_config judge_info 分别为

```json
[
  {
    "input": "1 2",
    "output": "2 4"
  },
  {
    "input": "2 3",
    "output": "4 6"
  }
]
```
```json
{
  "memory_limit": 32,
  "time_limit": 100
}
```

```json
{

  "info": "Accepted(类型种类可查看枚举类)",
  "memory": 12,
  "time": 46
}
```
使用MybatisX生成对应的mapper和service方法


## 2.7 添加dto、vo封装类
1. 创建题目
2. 修改题目
3. 搜索题目
其中搜索题目继承了PageResult, 支持分页查询, 测试用例在dto中使用List<JudgeCase>接收, 和前端json对应, 每一个JudgeCase是一个input output对











# userManage-backend

一个简单的登录注册和用户管理后端
java + springboot + mysql + mybatis plus
## 1.31 初始化SpringBoot3和Mybatis Plus项目
1. MybatisPlus 通过将Mapper接口继承BaseMapper, 实现基础单表增删改查操作
    > 注意: MybatisPlus对应SpringBoot3的依赖版本引入   
2. 用户基本信息数据库表设计
字段 字段类型 功能 索引
```mysql
create table user(
   id bigint primary key auto_increment comment '主键',
  
   username varchar(256) comment '用户昵称',
   user_account varchar(256) comment '用户账号',
   avatar varchar(1024) comment '头像url',
   gender tinyint comment '性别',
   password varchar(512) comment '密码',
  role tinyint default 0 not null comment '用户角色, 0默认用户',
   phone varchar(128) comment '电话',
   email varchar(256) comment '邮箱',
   status tinyint default 0 comment '用户状态',
   create_time datetime comment '创建时间',
   update_time datetime comment '更新时间',
  // mybatis plus 实现自动逻辑删除
   is_delete tinyint defalut 0 comment '数据是否删除(逻辑删除, 非直接删除)'
   )
```
   * 使用MybatisX Generator工具自动生成数据库表对应的domain实体类, mapper, service, serviceImpl
   * 数据库下划线转驼峰默认开启
   * mapper接口继承了BaseMapper(基本语句), service接口继承了IService, 实现常见Service方法
3. 用户注册
   * 用户在前端输入账户、密码
   * 校验用户账户、密码是否符合要求
     * 账户不少于6位, 不能重复, 只包含字母数字下划线, 不含特殊字符
     * 密码6位以上, 包含字母数字, 进行加密存储
     * 向数据库插入用户数据

使用Junit进行单元测试(测试路径和项目路径相同)

## 2.1 

1. 用户登录

   * 参数: 账户、密码

   * 请求类型:POST (user/register)

   * 返回类型: 用户信息(不含敏感信息)
     * 校验用户账户密码合法性
     * 数据库中密码是否正确, 和密文密码做对比
     * 记录用户登录态(用户信息, 存到HttpServerlet session中

2. 用户信息查询和删除(Todo: 用户身份鉴权, 分页查询)

   * 利用MybatisPlus中的list方法进行查询, 构造querywrapper进行模糊匹配
   * 利用MybatisPlus中的remove方法进行删除(自动逻辑删除)

3. 加入了一个用户权限字段user_role 区分管理员用户和普通用户

## 2.4 代码优化
1. 定义全局错误码ErrorCode, 以及异常类和全局异常处理器ExceptionHandler
    * 发生非正常逻辑时直接抛出自定义异常类
2. 全局登录校验和使用注解的身份校验拦截器Interceptor

## 2.6 分页查询, 条件查询
1. 分页查询, 注册Mybatis plus分页查询拦截器, 定义iPage传入参数
2. 条件查询, 使用queryWrapper定义条件查询条件, 支持字符串模糊搜索