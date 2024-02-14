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
tips: 封装类用包装类型, 前端没有传入值默认为null
其中搜索题目继承了PageResult, 支持分页查询, 测试用例在dto中使用List<JudgeCase>接收, 和前端json对应, 每一个JudgeCase是一个input output对

## 2.8 添加对题目的增删改查方法
1. 创建题目
    * 判断题目中字段是否合法, 将接收的json转为字符串存入数据库
2. 修改题目
    * 同样判断字段合法性, 并转为字符串
3. 搜索题目
    * 分为管理员搜索和用户搜索, 结果对用户隐藏了测试用例等信息, 包含了数据库中字符串转json数组和数据脱敏方法
4. 添加题目的提交记录(code, questionId, language);
5. 添加编程语言和提交状态的枚举类
6. 引入swagger接口文档测试

## 2.9
1. 添加了对提交记录的分页查询方法, 针对code字段, 只有本人或管理员可查看
2. 修改了对题目合法性判断的逻辑, 应当对前端原始数据进行判断, 而不是转换后的String进行判断
    * 使用StringUtils和ObjectUtils中的方法判断是否为null或者空
3. 改了一个小bug: 私有字段要getDeclaredField获取

## 2.10
1. 分页查询返回接口改为PageResult
2. question表中添加了难度字段

## 2.11
1. 定义三种代码沙箱
    * 模拟执行代码沙箱
    * 远程代码沙箱
    * 第三方代码沙箱
> 使用工厂模式, 由传入参数决定使用哪一种沙箱
> 参数配置化, 将可以自定义的参数, 写到application.yml文件中, 通过value注解或configurationProperties引用
> 静态代理模式, 通过静态代理在代码沙箱调用前后输出日志, 不改变代码沙箱的基础实现, 对功能进行增强 

2. 完成判题逻辑
    * 沙箱执行输出数量和预期是否相等
    * 判断每一项输出是否相等
    * 判断性能限制是否满足要求
3. 判题流程开发 + 策略模式
    * 对于不同语言, 定义不同的判题策略(例如Java允许多执行10s)
    * 定义JudgeManager根据语言类型不同执行不同的判题策略, 并在判题完成后及时更新提交记录
    * 使用CompletableFuture定义异步执行判题逻辑

## 2.12 
1. docker创建代码沙箱
   * 虚拟机:每个虚拟机都运行着完整的操作系统实例，包括其内核及完整的用户空间。虚拟机完全在虚拟的cpu, 内存运行(创建虚拟机时指定)
   * docker:操作系统级别的虚拟, 共享操作系统内核, 拥有独立的文件系统空间、网络空间、进程空间, 以及CGroups实现容器的资源隔离
   * docker利用linux的namespaces为容器提供独立的文件系统, 进程空间等视图, 使用CGroup限制docker使用的资源量
2. java操作docker
   * 使用docker-client封装http请求和docker守护进程进行交互
   * 具体步骤
      * 指定apache http_client5作为通信, 配置docker-config
      * 远程aliyun服务器docker守护进程开启tcp2375端口监听
      * 在aliyun官网和主机开启2375端口安全组, **重启远程服务器**
      * 测试远程控制docker操作(创建容器, 启动容器, 获取日志, 删除容器)
3. //todo docker代码沙箱实现

## 2.14 Java原生实现代码沙箱

1. 初始化spring项目作为代码沙箱接口API
   * javac 编译 java 执行
   * 使用Java进程管理Process执行代码
     * 保存用户代码文件
     * 编译代码, 得到class文件
     * 执行代码, 得到输出结果(将编译和执行的步骤抽象为一个运行命令的工具类, 并记录时间(Spring 的stopwatch对象), 获取最长时间用于判断是否总用时超时)
     * 整理输出结果
     * 出现系统错误或编译错误返回error(status = 2)





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
