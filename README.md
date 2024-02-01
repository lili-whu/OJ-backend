# userManage-backend

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

 