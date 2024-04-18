# init_sys
初始化后台项目模板，使用session登录，aop自定义注解鉴权，自定义工具库lms-utils，具备基本的用户管理功能
## 模板特点

### 主流框架 & 特性



- MyBatis + MyBatis Plus 数据访问（开启分页）
- Spring Boot 调试工具和项目处理器
- SA-token 认证授权框架
- Spring 事务注解

### 数据存储

- MySQL 数据库
- Redis 内存数据库





## 业务功能

- 用户登录、注册、注销、更新、检索、权限管理

- ## 用说明

部署：项目中使用了自己做的一个工具库starter，已经放在项目的mystarter文件夹下，项目涉及到的sql在sql文件夹下
如果你要启动项目的话需要使用

```
mvn install:install-file -Dfile=lms-utils-1.0-SNAPSHOT.jar -Dmaven.repo.local=D:\apache-maven-3.6.1\maven_repository -DgroupId=com.lms -DartifactId=lms-utils -Dversion=1.0-SNAPSHOT -Dpackaging=jar

```

在jar包所在路径下使用这条命令
其中-Dmaven.repo.local指定你的maven本地仓库路径，

另外前端修改需要在.env.development修改你的后端地址
