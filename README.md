# liOJ在线判题系统
使用 java + springboot + mysql + mybatis plus + docker + redis + rabbitMQ构建的OJ系统
#### 在线访问
http://121.199.10.73
#### 判题功能流程图
![img.png](./assest/oj流程.png)

#### 主要功能介绍
* 用户登录、注册、信息管理
* 题目编辑上传和修改(介绍、测试用例、时空限制、类型、难度、题解等)
* 根据标题、标签、类型查询题目, 代码提交记录查询
* Docker部署Java运行环境沙箱/本地直接编译执行用户代码(使用模版方法实现两种执行方式)
* 判题服务校验沙箱执行结果和标准测试用例输出
* 查看历史提交记录和记录详细信息

#### 优化
* Redis限制用户提交时间间隔(防止多次点击)
* RabbitMQ实现后端主服务和判题服务异步化解耦






## ——————————————————————————————
## 分割线以下为项目实现的的笔记

## 系统分析
后端代码沙箱编译代码, 运行代码, 输入测试用例, 判题服务获得结果, 比对测试结果

## 2.6 题目模块开发
1. 题目和题目提交数据库表设计

#### user表

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

## 2.14 Java原生实现代码沙箱

1. 初始化spring项目作为代码沙箱接口API
   * javac 编译 java 执行
   * 使用Java进程管理Process执行代码
     * 保存用户代码文件
     * 编译代码, 得到class文件
     * 执行代码, 得到输出结果(将编译和执行的步骤抽象为一个运行命令的工具类, 并记录时间(Spring 的stopwatch对象), 获取最长时间用于判断是否总用时超时)
     * 整理输出结果
     * 出现系统错误或编译错误返回error(status = 2)

2.  代码安全限制

   一些危险的破坏操作

   * 长时间时间占用 Thread.sleep()
   * 内存占用过大
   * 读系统目录文件, 信息泄露
   * 写入文件, 并执行

   解决方案

   * 守护线程, 限制运行时间超时控制

   * ```java
     // 守护线程, 超时控制
     new Thread(() -> {
         try {
             Thread.sleep(TIME_OUT);
             if(process.isAlive()){
                 process.destroy();
                 System.out.println("超时自动退出");
             }
         } catch (InterruptedException e) {
             throw new RuntimeException(e);
         }
     ```

   * 限制系统内存占用, 限制JVM最大堆内存占用

     JVM参数 -Xmx256m限制最大堆内存空间 -Xms指定初始堆内存空间

     > 只是在JVM应用程序(堆空间)上进行限制, 不能等同于系统实际占用的资源. 在Linux中可以使用cgroup限制对CPU和内存的分配

   * 定义代码黑名单, 禁止非法代码(正则表达式/布隆过滤器/字典树)

     使用hutool中的WordTree创建字典树并检测代码

     缺点: 无法遍历所有的黑名单, 不同编程语言对应的关键词都不一样

## 2.15

1. Docker 代码沙箱实现

* 每次提交有一个单独的容器环境运行代码, 接受多次输入和输出
* 创建容器, 将编译好的class文件复制到docker容器中
* 使用execCreateCmd创建在容器中执行的命令, 传入ResultCallBack回调函数, 得到代码执行的输出
  * 初始化容器时指定tty创建一个交互容器,容器不会关闭,  但不能指定其他cmd命令,否则执行完之后也会直接退出(坑: 解决时间20min)
  * 注意!!! 每一轮的ResultCallBack需要重新定义, 每一个cmd命令传入一个新的callback(解决时间1h)
  * 创建命令和执行命令需要两步exec, 带有awaitCompletion的是异步的方法, 返回void为同步的方法
  * 将文件拷贝到远程, 需要将文件压缩为tar, 需要一个压缩tar方法
* 获取占用时间和占用内存
  * 占用时间是测试用例的最长执行时间
  * 占用内存定期从监控数据Statics中获取
* 安全管理
  * 初始化容器时限制内存
  * 在awaitCompletion中定义最长执行时间,并在onComplete中判断是否超时
  * 容器隔离保障代码和文件安全

## 2.16 跑通项目流程 + 优化

1. 模版设计模式优化代码沙箱

* 定义一套执行流程, 并由子类负责每个步骤的具体实现, 这里代码沙箱的执行过程将抽象为5个步骤
* 定义abstract模版方法抽象类, 包含最基础方法流程(参考Java原生代码沙箱实现), 抽象类的不暴露给调用者的方法, 可以设置为protected(子类可见, 外部不可见)
* 继承抽象类, 如果没有重写则使用默认实现, 也可以重写或增强模版方法
  * 在调用父类方法时, 如果子类用重写相应的方法, 则使用子类重写后的方法(**多态**的体现)(动态绑定机制)(字段的访问不涉及多态, 取决于引用的类型)
* 定义Java原生代码沙箱, 复用模版方法, 在其基础上加入第0步检验非法操作
* 定义Docker代码沙箱, 重写runJavaCode方法(包含容器创建, 文件传输, 代码测试, 容器删除), 其他保持一致

2. 跑通代码流程, 策略模式(参数配置化)选择remoteCodeSandbox, 远程代码沙箱提供docker原生实现的API接口, 项目发送请求给代码沙箱, 处理后返回, 结果进行处理写入数据库

3. recordSubmit表新建字段result, 存储判题结果, 建立问题id和提交用户id的联合索引, 便于用户查询题目的提交记录; judgeInfo新增详细描述, 用于返回错误信息

4. todo 使用Redis记录用户提交ID, 限制用户10秒内只允许一次提交

5. 定义CodeSandboxStatus枚举类, 表示判题沙箱的返回状态(正常执行完成, 系统错误, 编译错误等), 完善判题服务逻辑, 根据代码沙箱返回状态判断代码执行结果 // todo 内存溢出尚未判断

## 2.17 

1. 添加提交记录查询接口
   * 根据用户id和题号查询用户提交记录
   * 在用户问题查询中关联提交记录信息, 进行查询
     * 详细信息, 通过题号和提交记录id单独查询
     * 在查询Quesiton列表中使用**批量查询/ foreach动态sql实现对所有问题的一次查找**, 查询最近一次的提交状态, 结果按照创建时间排序并使用hash表去重, 保留最后一次提交信息, 设置到submitStatus中
     * 在用户根据题目id查询详细信息方法中, 同时关联查询用户的所有提交记录
2. **bug** 在创建openapi接口时, questionUserVO中的RecordSubmitVO总是无法创建接口

 RecordSubmitVO中包含了questionUserVO字段, 产生了循环依赖, 导致文档错误, 所以无法生成, 但可以正常执行返回, 因为返回时没有循环依赖(20min)

3. 题目界面优化: 自动加载用户的上一次提交代码, 显示上一次的提交结果

## 2.18


5. 对其他数据结构(引用类型输入的支持, 二叉树, List)

1. 后端添加了阿里云oss文件上传方法配置,用于用户上传图片
2. 完善了用户修改个人信息的方法

* 新增SafetyUserDTOByUser, 用于用户传递修改的信息
* 管理员和用户修改分开校验
* 当用户修改信息之后, 重新设置登录的session

3. 参数配置化, 将参数写入配置文件中

* 变量不能定义为static final, 必须是sping bean, 必须通过依赖注入调用类

4. 修改工厂模式, 在configuration中定义bean, 然后在工厂类中声明component, 通过ApplicationContext得到spring bean并返回
5. 安全性配置
* 在判题服务和代码沙箱中约定一个key value, 设置在请求头header中, 代码沙箱对请求进行鉴权
6. 配置redis 和 session 实现将session存储在redis中, 实现分布式登录
7. redis实现限流, 设定过时时间10s, 不允许10s内重复提交

## 2.20
1. 采用rabbit消息队列, 将题目提交和判题服务的异步操作使用mq解耦
   * rabbit消息队列包含交换机, 队列, 
   * 发送消息时指定交换机和routineKey, exchange根据routineKey转发到相应的队列
   * 接收消息绑定某一个队列, 并进行处理, 处理完毕后发送ack确认
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

2. 用户信息查询和删除

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
