# DS项目笔记

# 1、EZDML工具

配置： 工具—>修改ini配置

```markdown
[DefaultFieldTypes] 
[CustFieldTypes] 
1=bigint
2=decimal
[DbConn]
```

# 2、Mybatis通用Mapper使用(CRUD)

## 1、搭建module

<img src="images/image-20210729133550491.png" alt="image-20210729133550491" style="zoom: 67%;" />

**依赖选Web和Mysql, Jdbc,MyBatis**

<img src="images/image-20210729133903004.png" alt="image-20210729133903004" style="zoom: 50%;" />

## 2、配置通用Mapper

 **在pom.xml文件中，加入**

```xml
<!-- 通用mapper -->
<dependency>
    <groupId>tk.mybatis</groupId>
    <artifactId>mapper-spring-boot-starter</artifactId>
	<version>1.2.3</version>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-jdbc</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

在TestCommonmapperApplication.java中添加如下注解， ==注意通用mapper是tk.mybatis.mapper==

```java
@SpringBootApplication
@MapperScan(basePackages = "com.wgml.commonmapper.usermanage.mapper")
public class TestCommonmapperApplication {

    public static void main(String[] args) {
        SpringApplication.run(TestCommonmapperApplication.class, args);
    }

}
```

## 3、配置数据源

在application.properties中

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/itaomall?characterEncoding=UTF-8&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver

mapper.identity=MYSQL   #这个需要配置 否则报错
```

**表结构**

```sql
CREATE TABLE `user_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `login_name` varchar(200) DEFAULT NULL COMMENT '用户名称',
  `nick_name` varchar(200) DEFAULT NULL COMMENT '用户昵称',
  `passwd` varchar(200) DEFAULT NULL COMMENT '用户密码',
  `name` varchar(200) DEFAULT NULL COMMENT '用户姓名',
  `phone_num` varchar(200) DEFAULT NULL COMMENT '手机号',
  `email` varchar(200) DEFAULT NULL COMMENT '邮箱',
  `head_img` varchar(200) DEFAULT NULL COMMENT '头像',
  `user_level` varchar(200) DEFAULT NULL COMMENT '用户级别',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='用户表'
```

## 4、代码开发

| 包           | 类                    | 说明       |
| ------------ | --------------------- | ---------- |
| controller   | UserManageController  | web        |
| service      | UserManageService     | 接口       |
| service.impl | UserManageServiceImpl | 实现类     |
| bean         | UserInfo              | 实体bean   |
| mapper       | UserInfoMapper        | mapper接口 |

### 4.1 bean

```java
package com.wgml.commonmapper.usermanage.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * @author oislfy
 * @description 实体类
 * @since JDK1.8
 */
@Data
public class UserInfo {
        @Id
        @Column
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private String id;
        @Column
        private String loginName;
        @Column
        private String nickName;
        @Column
        private String passwd;
        @Column
        private String name;
        @Column
        private String phoneNum;
        @Column
        private String email;
        @Column
        private String headImg;
        @Column
        private String userLevel;
}
```

**注意**：@Column 和@Id 都是javax.persistence包中的

**技巧 idea快捷键**：alt+insert可以快速插入getter 和setter  ，@Data需安装idea插件lombok和导入依赖

### 4.2 Mapper

```java
public interface UserInfoMapper extends Mapper<UserInfo> {
}
```

==注意： Mapper也是引用tk.mybatis.mapper.common.Mapper包中的==

### 4.3 Idea校验

Idea 有的时候校验@Autowired不准 可以把校验关闭  或者 在该属性上使用@SuppressWarnings("all")注解抑制烦人的警告

settings -> Inspections -> spring->spring core -> code-> Autowiring for Bean class

![image-20210729140943073](images/image-20210729140943073.png)

### 4.4 接口声明UserService

```java
public interface UserService {

    List<UserInfo> getUserInfoListAll();

    void addUser(UserInfo userInfo);

    void updateUser(UserInfo userInfo);

    void updateUserByName(String name,UserInfo userInfo);

    void delUser(UserInfo userInfo);

}
```

### 4.5 接口实现UserServiceImpl

```java
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserInfoMapper userInfoMapper;
    @Override
    public List<UserInfo> getUserInfoListAll() {
        return userInfoMapper.selectAll();
    }

    @Override
    public void addUser(UserInfo userInfo) {

        userInfoMapper.insert(userInfo);
    }

    @Override
    public void updateUser(UserInfo userInfo) {
        userInfoMapper.updateByPrimaryKeySelective(userInfo);
    }

    @Override
    public void updateUserByName(String name, UserInfo userInfo) {
        Example example = new Example(UserInfo.class);
        example.createCriteria().andEqualTo("name",name);
        userInfoMapper.updateByExampleSelective(userInfo,example);
    }

    @Override
    public void delUser(UserInfo userInfo) {
        userInfoMapper.deleteByPrimaryKey(userInfo);
    }
}
```

### 4.6 Controller

```java
@Controller
public class UserController {

    @Autowired
    private UserService userService;
    @RequestMapping("findAll")
    @ResponseBody
    public List<UserInfo> findAll(){
        return userService.getUserInfoListAll();
    }

    @RequestMapping("add")
    @ResponseBody
    public void add(){
        UserInfo userInfo = new UserInfo();
        userInfo.setEmail("test@qq.com");
        userInfo.setLoginName("test");
        userInfo.setName("testQQ");
        userInfo.setPasswd("test");
        userService.addUser(userInfo);
    }

    @RequestMapping("upd")
    @ResponseBody
    public void upd(){
        UserInfo userInfo = new UserInfo();
        userInfo.setId("4");
        userInfo.setNickName("testAdmin-test");
        userService.updateUser(userInfo);
    }

    @RequestMapping("upd1")
    @ResponseBody
    public void upd1(String name){
        UserInfo userInfo = new UserInfo();
        userInfo.setNickName("testAdmin-test");
        userService.updateUserByName(name,userInfo);
    }

    @RequestMapping("del")
    @ResponseBody
    public void del(){
        UserInfo userInfo = new UserInfo();
        userInfo.setId("4");
        userService.delUser(userInfo);
    }
}
```







# 3、 hosts工具

用来修改：

C:\Windows\System32\drivers\etc\hosts文件的！

<img src="images/image-20210729184331980.png" alt="image-20210729184331980" style="zoom: 150%;" />



如果系统中没有该文件

New 创建



# 4、新增一个虚拟机  修改ip地址

Mysql，redis，zookeeper，tomcat，jdk，elasticsearch，activemq。

##    克隆虚拟机

​     创建一个完整的虚拟机   被克隆的虚拟机一定能要处于关闭状态！

## 1. 修改ip地址

1命令：vim /etc/sysconfig/network-scripts/ifcfg-ens33

```shell
#原有配置：
							
							TYPE=Ethernet
							PROXY_METHOD=none
							BROWSER_ONLY=no
							BOOTPROTO=dhcp
							DEFROUTE=yes
							IPV4_FAILURE_FATAL=no
							IPV6INIT=yes
							IPV6_AUTOCONF=yes
							IPV6_DEFROUTE=yes
							IPV6_FAILURE_FATAL=no
							IPV6_ADDR_GEN_MODE=stable-privacy
							NAME=ens33
							UUID=4d9d8a10-2c0b-4aa0-9451-ceca94f1e555
							DEVICE=ens33
							ONBOOT=no						
```

```shell
#2修改之后配置：
							BOOTPROTO=static
							NAME=ens33
							DEVICE=ens33
							ONBOOT=yes
							IPADDR=192.168.72.139   #修改
							NETMASK=255.255.255.0
							GATEWAY=192.168.72.2   #修改
							DNS1=114.114.114.114
```

a)    静态ip 【static】 生产环境推荐  所有的服务器ip 地址都是静态配置！

b)    动态ip 【dhcp】

c)    ifcfg-ens33：网卡

i.      生产环境是没有图形化界面！必须通过命令修改

```shell
ONBOOT=yes 
BOOTPROTO=static
IPADDR=192.168.67.215
NETMASK=255.255.255.0
GATEWAY=192.168.67.2
DNS1=114.114.114.114
#DNS2=8.8.8.8
#DNS3=192.168.67.2
```

3 配置完成之后 需要重启网络 service network restart            reboot

为什么网关：是192.168.67.2

在虚拟机中规定好了！

重点：必须要保证当前虚拟机可以上网！
				   如果不能上网，则不能使用yum 命令进行安装任何软件
				   dhcp：动态 不推荐使用！			

## 2. 配置网络链接

a)    ping [www.baidu.com](http://www.baidu.com)

## 3. 虚拟机不能上网如何解决？

a)    第一时间看DNS 配置

b)    第二时间看服务是否开启 【services.msc】

![image-20210729184431598](images/image-20210729184431598.png)



# 5、Linux安装mysql5.7

```shell
# rpm 安装  rpm -ivh xxx.rpm
# rpm 卸载 rpm -e –nodeps xxx.rpm
```

```shell
----- linux 安装mysql -----

	0.  导入jar包 到opt/mysql
	
	1.	检查 
		 rpm -qa|grep mariadb
		 MariaDB数据库管理系统是MySQL的一个分支，主要由开源社区在维护，采用GPL授权许可
		。开发这个分支的原因之一是：甲骨文公司收购了MySQL后，有将MySQL闭源的潜在风险，
		因此社区采用分支的方式来避开这个风险。
		
		如果存在则卸载
		请先执行卸载命令：rpm -e --nodeps mariadb-libs-5.5.56-2.el7.x86_64 
		
		必须有如下两个包
		
		rpm -qa|grep libaio
		 
		rpm -qa|grep net-tools
		
	2.	授权 chmod -R 777 /tmp
	
	3.	安装mysql
		rpm -ivh mysql-community-common-5.7.16-1.el7.x86_64.rpm 
		rpm -ivh mysql-community-libs-5.7.16-1.el7.x86_64.rpm
		rpm -ivh mysql-community-client-5.7.16-1.el7.x86_64.rpm 
		rpm -ivh mysql-community-server-5.7.16-1.el7.x86_64.rpm
		
	4.	查看版本
		mysqladmin --version命令
		
	5.	初始化服务
		mysqld --initialize --user=mysql
		
	6.	查看密码
		cat /var/log/mysqld.log
		root@localhost: 后面就是初始化的密码
		
		
		
	7.	相关服务：
		启动：systemctl start mysqld.service
		停止：systemctl stop mysqld.service
		
		查看是否启动成功
		systemctl status mysqld  出现绿色的running为成功

		
	8.	登录修改密码
		mysql -uroot -p
		
		ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password'; 
		ALTER USER 'root'@'localhost' IDENTIFIED BY '123123';
		
		退出并测试新密码
		Quit 退出
		
		查看mysql是否为自启动
		systemctl list-unit-files|grep mysqld

		
	9.	修改字符集
		vim  /etc/my.cnf
		在最后加上中文字符集配置
		character_set_server=utf8
		
        skip-name-resolve 表示跳过dns解析 以后连接mysql就不能使用域名方式连接！
        Demo： 192.168.67.112 mysql.itaomall.com  

		并重启
		systemctl restart mysqld.service
	10.	授权远程连接
		再次登录mysql 授予远程连接
		
		grant all privileges on *.* to root @'%'  identified by '123123'; 
		
		刷新权限：
		
		flush privileges;
		
		关闭防火墙 
		一次关闭
		systemctl stop firewalld.service
		永久关闭
		systemctl disable firewalld.service

		
		并重启
		systemctl restart mysqld.service
		
		导入gmall.sql 文件
		source /root/gmall.sql
	

```

详细步骤：

```shell
#1.linux安装mysql5.7顺序
#①mysqladmin –version 查看版本号
#②mysql5.7安装在linux中需要先初始化
  Mysqld –initialize –user=mysql
#③查看初始化密码
 Cat /var/log/mysqld.log   在@localhost后面的为初始化密码
#④启动mysql
Systemctl start mysqld
#⑤查看是否启动成功
Systemctl status mysqld  出现绿色的running为成功
#⑥登陆mysql
Mysql  -uroot -p初始化密码
#⑦修改密码
Alter user ‘root’@’localhost’ identified by ‘123456’;
#⑧退出并测试新密码
Quit 退出
#⑨查看mysql是否为自启动
Systemctl list-unit-files|grep mysqld
#2.解决mysql中文乱码问题
#①修改配置文件 、etc/mysql.conf
#②修改已经存在库、表的字符集 
alter database mydb character set 'utf8;
alter table mytbl convert to character set 'utf8';
#③表中存在乱码的数据删除或者update
#3.查看user表改成列式显示
select * from user\G;
#4.客户端（SqlYoung等）远程连接，需要创建用户并且授权
#①create user  ‘zhangsan’ identified by '123456';表示创建用户zhangsan,密码为123456
 #  但只是创建了用户，还需要授权才能访问相应的库和表。
#②grant all privileges on *.* to root @'%' identified by '123456';
#表示创建root用户,密码为123456,并且授予所有访问ip(@%)、所有库表(*.*)的访问权限
#修改user表里的操作要flush privileges 才能生效
#③查看防火墙是否开启：(5.7版本)
systemctl list-unit-files|grep firewalld
systemctl stop firewalld #关闭防火墙
systemctl disable firewalld #禁止防火墙开机启动
#④如果以上都没问题，但客户端远程连接时还是出现10060,10038错误
 #可以尝试再开放一下3306端口（netstat -talnp 查看端口，即使有3306也可尝试此步）
#(1)firewall-cmd --zone=public --add-port=3306/tcp -permanent
#(2)firewall-cmd –reload
#(3)重启一下：
systemctl restart mysqld

```



# 6、Dubbo和zookeeper

Dubbo 正常运行必须依赖zookeeper！zookeeper 什么语言[java]

###  配置jdk 步骤：注意：jdk-1.8

rpm -qa | grep jdk

![image-20210731002555200](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002555200.png)

[root@localhost opt]# tar -zxvf jdk-8u152-linux-x64.tar.gz

\# vim /etc/profile

在最后添加如下配置

```shell
JAVA_HOME=/opt/jdk1.8.0_152
CLASSPATH=$JAVA_HOME/lib
PATH=/opt/jdk1.8.0_152/bin:$PATH
export JAVA_HOME CLASSPATH PATH
```



![image-20210731002607784](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002607784.png)

\# source /etc/profile

使配置文件生效。永久生效则必须要reboot！

\# java -version

![image-20210731002622878](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002622878.png)

那dubbo和zookeeper如何引入？

dubbo其实是一组jar包，通过maven引入就可以。

zookeeper是一个开源的服务软件，需要安装到linux中。 

### 1 安装zookeeper

#### 1.1安装环境： 

  linux版本: CentOS 7.0

zookeeper版本 zookeeper-3.4.11.tar.gz

拷贝zookeeper-3.4.11.tar.gz到/opt下，并解压缩

![image-20210731002651688](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002651688.png)                   

改名叫zookeeper

![image-20210731002710435](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002710435.png)

#### 1.2初始化zookeeper配置文件

拷贝/opt/zookeeper/conf/zoo_sample.cfg 

到同一个目录下改个名字叫zoo.cfg

![image-20210731002723240](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002723240.png)

然后咱们启动zookeeper

![image-20210731002735611](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002735611.png)

以上状态即为安装成功。

默认的端口号是2181!

### 2  dubbo的监控中心使用

dubbo本身并不是一个服务软件。它其实就是一个jar包能够帮你的java程序连接到zookeeper，并利用zookeeper消费、提供服务。所以你不用在Linux上启动什么dubbo服务。

但是为了让用户更好的管理监控众多的dubbo服务，官方提供了一个可视化的监控程序，不过这个监控即使不装也不影响使用。

#### 2.1安装监控软件： 

注意zookeeper版本，3,6以上版本中有个内嵌的管理控制台是通过jetty启动，也会占用8080 端口。 解决办法修改tomcat端口号

在server.xml文件修改



配置：  tomcat8 + dubbo-admin.war + jdk1.8

扩展：**tomcat7+dubbo-admin.2.5.3.war+jdk1.7**

dubbo-admin.war 是一个动态web项目

 拷贝tomcat8和dubbo-admin.war到/opt目录下

![image-20210731002814490](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002814490.png)

然后把dubbo-admin-2.6.0.war拷贝到tomcat的webapps目录下

![image-20210731002825385](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002825385.png)

#### 2.2启动后用浏览器访问

![image-20210731002838559](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002838559.png)

可以看到要提示用户名密码，默认是root/root

Dubbo.properties.配置文件中写明了，密码是什么以及注册中心的地址！

(修改的话，可以去)

![image-20210731002852075](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731002852075.png)

打开这个界面就说明，dubbo的监控服务已经启动。但是现在咱们还没有搭建dubbo的提供端和消费端。

### 3 开发功能

通过用户的id 查找用户的地址信息   springboot1.5.21.RELEASE

#### 3.1 引入dubbo的依赖

*spring-boot-starter-dubbo*

*dubbo*

*zkclient*

这个依赖首先要放到gmall-parent工程中，用来定义要引入的三个包是什么版本。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.atguigu.gmall</groupId>
    <artifactId>gmall-parent</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>pom</packaging>
    <properties>
        <fastjson.version>1.2.46</fastjson.version>
        <dubbo-starter.version>1.0.10</dubbo-starter.version>
        <dubbo.version>2.6.0</dubbo.version>
        <zkclient.version>0.10</zkclient.version>
    </properties>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>fastjson</artifactId>
                <version>${fastjson.version}</version>
            </dependency>
            <dependency>
                <groupId>tk.mybatis</groupId>
                <artifactId>mapper</artifactId>
                <version>${mapper.version}</version>
            </dependency>
 
            <dependency>
                <groupId>com.alibaba</groupId>
                <artifactId>dubbo</artifactId>
                <version>${dubbo.version}</version>
            </dependency>

            <dependency>
                <groupId>com.101tec</groupId>
                <artifactId>zkclient</artifactId>
                <version>${zkclient.version}</version>
            </dependency>

            <dependency>
                <groupId>com.gitee.reger</groupId>
                <artifactId>spring-boot-starter-dubbo</artifactId>
                <version>${dubbo-starter.version}</version>
            </dependency>

        </dependencies>
    </dependencyManagement>
</project>

```

然后加入到gmall-common-util模块中

```xml
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>dubbo</artifactId>
</dependency>

<dependency>
    <groupId>com.101tec</groupId>
    <artifactId>zkclient</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-log4j12</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>
    <groupId>com.gitee.reger</groupId>
    <artifactId>spring-boot-starter-dubbo</artifactId>
</dependency>
```

这样在所有的业务模块中都可以使用dubbo了。

#### 3.2 如何使用： 

dubbo的使用分为**提供端**和**消费端**。使用起来非常方便只要记住两个注解@Reference和@Service，加上application.properties的一段配置就可以了。

 

#### 3.3  提供端

顾名思义就是提供服务供别人调用的，相当于spring中的Service的实现类。

使用也很简单，就是一个注解加一份配置

提供端在实现类上增加注解 @Service，和spring的是一样的但是引的包是不一样的。如下

![image-20210731003047524](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003047524.png)

在UserInfoServiceImpl实现类上重新引包，这次引入com.alibaba.dubbo.config.annotation这个包。

**在gmall-user-manage**项目中为配置文件application.properties中增加

```properties
#dubbo配置
spring.dubbo.application.name=userservice  #可改
spring.dubbo.registry.protocol=zookeeper    #不可改
spring.dubbo.registry.address=192.168.72.139:2181  #实际情况
spring.dubbo.base-package=com.wgml.itmall    #实际情况   也可以用注解配置
spring.dubbo.protocol.name=dubbo    #不可改
```

其中：

application.name就是服务名，不能跟别的dubbo提供端重复

registry.protocol 是指定注册中心协议

registry.address 是注册中心的地址加端口号

base-package 注解方式要扫描的包

protocol.name 协议名称dubbo

注意：防火墙要关闭，或者是放行2181端口号

 

#### 3.4 消费端

gmall-order-web模块 application.properties配置

```properties
spring.dubbo.application.name=order-web
spring.dubbo.registry.protocol=zookeeper
spring.dubbo.registry.address=192.168.67.201:2181
spring.dubbo.base-package=com.atguigu.gmall
spring.dubbo.protocol.name=dubbo
spring.dubbo.consumer.timeout=10000
spring.dubbo.consumer.check=false
```

consumer.timeout 是访问提供端服务的超时时间，默认是1000毫秒

consumer.check 是启动消费端时，是否检查服务端能否正常访问。如果选择true，那启动消费端时，必须保证提供端服务正常，否		则接口无法注入。

**消费端代码**

 使用起来也比较简单，只要把原来@Autowired改成@Reference就可以 注意引用的包是

```java
com.alibaba.dubbo.config.annotation.Reference
```

不要引用错了

![image-20210731003307786](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003307786.png)

#### 3.5 启动测试

然后访问

![image-20210731003321461](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003321461.png)

说明controller可以通过dubbo调用不同模块的service

我们也可以通过dubbo-admin进行观察：

消费端

![image-20210731003426947](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003426947.png)

提供端

![image-20210731003442848](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003442848.png)

那么我们的分布式就可以基于这种方式实现不同模块间的调用。每一个实现服务的消费端和提供端分离。

![image-20210731003453828](C:/Users/yelinfang/AppData/Roaming/Typora/typora-user-images/image-20210731003453828.png)

#### 3.6 设置zookeeper开机自启动 【选做】

1. 进入目录

   a)    cd  /etc/rc.d/init.d

2. 创建zookeeper文件

   a)    vim zookeeper

3. 编辑zookeeper 文件

   ```shell
   #!/bin/bash
   #chkconfig: 2345 10 90
   #description: service zookeeper
   export   JAVA_HOME=/opt/jdk1.8.0_152
   export   ZOO_LOG_DIR=/opt/zookeeper/log
   ZOOKEEPER_HOME=/opt/zookeeper
   su    root    ${ZOOKEEPER_HOME}/bin/zkServer.sh      "$1"  #start的意思
   
   ```

   1. 授予可执行权限

      a)    chmod +x /etc/rc.d/init.d/zookeeper

   2. 添加到服务中,并查看是否添加成功

      a)    [root@localhost init.d]# chkconfig --add zookeeper

      b)    chkconfig --list

   3. 测试重启虚拟机

      a)    #reboot

### 4 另外版

```markdown
在linux 中安装dubbo 的环境
					5.4.1	安装jdk
							a.	先卸载jdk原始版本
								rpm -qa | grep jdk
								rpm -e --nodeps 
							b.	安装我们的jdk 版本
								JAVA_HOME=/opt/jdk1.8.0_152
								PATH=$PATH:$JAVA_HOME/bin
								CLASSPATH=$JAVA_HOME/lib
								export JAVA_HOME PATH CLASSPATH
								
								临时生效：source /etc/profile
								永久生效：reboot
					5.4.2	安装zookeeper
							a.	解压
							b.	进入解压之后的目录找conf 进行拷贝
									cp zoo_sample.cfg zoo.cfg
							c.	进入bin目录执行文件
									./zkServer.sh start
									./zkServer.sh status
					5.4.3	安装tomcat
								a.	解压
								b.	改名 mv apache-tomcat-8.5.24 tomcat8
								c.	运行
								d.	测试
					5.4.4	安装一个监控中心
							dubbo 监控中心实质：是一个 xxx.war包 动态web 工程！
							a.	把监控中心放入tomcat中webapps 目录下，此时会自动解压！前提条件是：tomcat 启动！
							b.	把tomcat 关掉！
								./shutdown.sh 
									在关闭的时候，一定要注意不能报错！
							c.	将tomcat 中webapps 中的dubbo-admin-2.6.0.war 删掉！
									rm -rf dubbo-admin-2.6.0.war
							d.	修改名称：mv dubbo-admin-2.6.0 dubbo-admin
							e.	重启tomcat
									./startup.sh 
									访问http://192.168.67.219:8080/dubbo-admin/ 输入用户名，密码 root root
									
									
							f.	正常进入监控中心
									http://192.168.67.219:8080/dubbo-admin/governance/providers
				5.5	如何使用dubbo
						两个注解 两个配置！
						Service注解暴露服务
						Reference注解引用服务
						
					配置服务提供者：
					5.5.1	引入依赖
						gmall-common-util 中引入
						<dependency>
						<groupId>com.alibaba</groupId>
						<artifactId>dubbo</artifactId>
						</dependency>

						<dependency>
						<groupId>com.101tec</groupId>
						<artifactId>zkclient</artifactId>
						<exclusions>
							<exclusion>
								<groupId>org.slf4j</groupId>
								<artifactId>slf4j-log4j12</artifactId>
							</exclusion>
						</exclusions>
						</dependency>

						<dependency>
						<groupId>com.gitee.reger</groupId>
						<artifactId>spring-boot-starter-dubbo</artifactId>
						</dependency>
						
						服务提供者：依赖的模块 service-util
						服务消费者：依赖的模块 web-uitl
						service-util，web-util 都依赖于 common-util
						
					5.5.2	
						添加配置文件信息
						spring.dubbo.application.name=usermanage
						spring.dubbo.registry.protocol=zookeeper
						spring.dubbo.registry.address=192.168.67.219:2181
						spring.dubbo.base-package=com.atguigu.gmall0218
						spring.dubbo.protocol.name=dubbo
					5.5.3	添加注解 @Service
					
					5.5.4	测试！
							注意：防火墙要关闭 linux ，本地都需要关闭！
							
					配置消费者：
						
						a.	添加注解 Reference注解引用服务
								@Reference
						b.	添加配置
								spring.dubbo.application.name=order-web
								spring.dubbo.registry.protocol=zookeeper
								spring.dubbo.registry.address=192.168.67.219:2181
								spring.dubbo.base-package=com.atguigu.gmall0218
								spring.dubbo.protocol.name=dubbo
								spring.dubbo.consumer.timeout=10000
								spring.dubbo.consumer.check=false
				
				项目关键：
					注册中心上！
						zk Cluster！
					生成环境：zk 集群 ，开启监控zk状态！
					
					监控中心可以不用开启！
						可以使用zkCli.sh 观察是否有服务提供者！
					
					*** 自启动！	
						cd /etc/rc.d/init.d
						
						vim zookeeper
							#!/bin/bash
							#chkconfig: 2345 10 90
							#description: service zookeeper
							export   JAVA_HOME=/opt/jdk1.8.0_152
							export   ZOO_LOG_DIR=/opt/zookeeper/log
							ZOOKEEPER_HOME=/opt/zookeeper
							su    root    ${ZOOKEEPER_HOME}/bin/zkServer.sh      "$1"
							
						chmod +x zookeeper
						
						chkconfig --add zookeeper
						chkconfig --list
						
						reboot
```



# 7、spring cloud 与 dubbo 区别？

解决分布式问题：
		微服务：spring boot + spring cloud + mybatis + mybatis-plus + redis + mysql + es + nginx + activemq + mycat/sharding JDBC 
		服务治理+服务调用: dubbo

		1.	数据传输方式：
						dubbo			二进制方式
						spring cloud	http
		2.	数据访问方式：
						dubbo			rpc
						spring cloud	restful 
		3.	功能来讲：
						dubbo			服务的治理
						spring cloud	17 个组件 
		4.	本质来讲：
						dubbo			jar 包
						spring cloud	spring source (spring全家桶)中的一员
		5.	开发角度：
						dubbo：			停止更新5年！ 依赖。dubbo.version !          2.5.3-jdk1.7-tomcat7
						spring cloud 	不需要注意jdk和tomcat版本是否匹配问题，
		6.	分布式角度
						dubbo：			传输数据方式要优于spring cloud！
						
		新的项目选用：spring boot + spring cloud + mybatis + mybatis-plus/mapper + redis + mysql + es + nginx + activemq + mycat/sharding JDBC + docker  + k8s/jenkins	

 
















































# 8、FastDfs文件服务器

见详细文档《FastDfs安装文档》

## spu的保存功能中的图片上传

## 1文件服务器

​     文件，图片存储需要注意的：

​     图片名称不能重复

​     图片的后缀名，保存跟源文件后缀名一致

​     文件服务器-长期开着的

现在咱们实现了文件从客户端提交，并展示的功能。服务器端要做的就是接收文件流，保存起来，并且返回给客户端文件的访问地址。

传统的用io流保存到web服务器本地的方式，可以直接用当前web服务的路径+图片名称来访问。

但是类似于商品图片这种海量级文件，光靠web服务器的硬盘是无法满足的。

另外如果，web服务器是集群的那么A服务器是没法访问B服务器的本地文件的。

所以需要把文件服务单独管理起来，成为文件服务器。

实现方式就是nginx+FastDFS

### 2FastDFS介绍

FastDFS 是一个由 C 语言实现的开源轻量级**分布式文件系统**，作者余庆(happyfish100)，支持 Linux、FreeBSD、AID 等 Unix 系统，解决了大数据存储和读写负载均衡等问题，适合存储 4KB~500MB 之间的小文件，如图片网站、短视频网站、文档、app 下载站等，UC、京东、支付宝、迅雷、酷狗等都有使用。

​                                ![image-20210804120433476](images/电商课程安排.bmp) 该软件作者是**阿里巴巴**大牛、chinaUnix版主**余庆**个人独立开发的

### 3FastDFS上传下载的流程

![image-20210804120509231](images/image-20210804120509231.png)

只要 storage 返回图片的路径图片名称，我们就能通过浏览器来访问图片了？

图片服务器在linux ？ nginx 做反向代理{图片服务器}！

### 4安装步骤参见《FastDFS安装说明》

### 5利用Java客户端调用FastDFS

服务器安装完毕后，咱们通过Java调用fastdfs。

加载Maven依赖

fastdfs 没有在中心仓库中提供获取的依赖坐标。

只能自己通过源码方式编译，打好jar 包，安装到本地仓库。

官方仓库地址：

https://github.com/happyfish100/fastdfs-client-java

在**项目的根目录**下使用git clone 命令，将代码下载

![image-20210804120533066](images/image-20210804120533066.png)

直接用idea 直接把这个源码作为模块导入工程

![image-20210804120557715](images/image-20210804120557715.png)

别的不用改，只把pom.xml中的版本改成1.27。

![image-20210804120613804](images/image-20210804120613804.png)

然后右边 执行install 就好了

![image-20210804120630547](images/image-20210804120630547.png)

安装好了 ，在gmall-manage-web模块就可以直接使用这个坐标了

```xml
<!--添加fdfs依赖-->
<dependency>
   <groupId>org.csource</groupId>
   <artifactId>fastdfs-client-java</artifactId>
   <version>1.27</version>
</dependency>
```

然后可以进行一下**上传的测试** 

在项目resource中添加tracker.conf 配置文件

![image-20210804120709312](images/image-20210804120709312.png)

```java
tracker_server=192.168.67.162:22122

# 连接超时时间，针对socket套接字函数connect，默认为30秒
connect_timeout=30000

# 网络通讯超时时间，默认是60秒
network_timeout=60000
    
===============分割线==================================================    
    
package com.wgml.itmall.manage;

import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ItmallManageWebApplicationTests {

    @Test
    public void textFileUpload() throws IOException, MyException {
        String file = this.getClass().getResource("/tracker.conf").getFile();
        ClientGlobal.init(file);
        TrackerClient trackerClient=new TrackerClient();
//        TrackerServer trackerServer=trackerClient.getConnection();
        TrackerServer trackerServer = trackerClient.getTrackerServer();
        StorageClient storageClient=new StorageClient(trackerServer,null);
        String orginalFilename="C:\\Users\\yelinfang\\Pictures\\Camera Roll\\Saved Pictures\\default.jpg";
        String[] upload_file = storageClient.upload_file(orginalFilename, "jpg", null);
        for (int i = 0; i < upload_file.length; i++) {
            String s = upload_file[i];
            System.out.println("s = " + s);
        }
    }

}



```

打印结果

![image-20210804120749549](images/image-20210804120749549.png)

这个打印结果实际上就是我们访问的路径，加上服务器地址我们可以拼接成一个字符串

```java
http://file.gmall.com/group1/M00/00/00/wKhDyVsLn96APCdkAACGx2c4tJ4983.jpg
```

直接放到浏览器去访问

![image-20210804120818602](images/image-20210804120818602.png)

上传成功！

 

对接到业务模块中

在修改FileUploadController的方法

服务器的地址：要实现软编码！硬编码！

注意：控制器需要跨域！ @CrossOrigin

```java
package com.wgml.itmall.manage.controller;

import org.apache.commons.lang3.StringUtils;
import org.csource.common.MyException;
import org.csource.fastdfs.ClientGlobal;
import org.csource.fastdfs.StorageClient;
import org.csource.fastdfs.TrackerClient;
import org.csource.fastdfs.TrackerServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Ye Linfang
 * @date 2021/8/4 17:48
 * @description TODO
 * @since JDK1.8
 */
@RestController
@CrossOrigin
public class FileUploadController {

    @Value("${fileServer.url}")
    private String fileUrl;

    @RequestMapping(value = "/fileUpload", method = RequestMethod.POST)
    public String fileUpload(@RequestParam("file") MultipartFile file) throws IOException, MyException {
        String imgUrl = null;
        if (file != null) {
            System.out.println("multipartFile = " + file.getName() + "|" + file.getSize());

            String configFile = this.getClass().getResource("/tracker.conf").getFile();
            ClientGlobal.init(configFile);
            TrackerClient trackerClient = new TrackerClient();
//        TrackerServer trackerServer=trackerClient.getConnection(); 1.27版本
            TrackerServer trackerServer = trackerClient.getTrackerServer();
            StorageClient storageClient = new StorageClient(trackerServer, null);

            String fileName = file.getOriginalFilename();
            String extName = StringUtils.substringAfterLast(fileName, ".");

            String[] upload_file = storageClient.upload_file(file.getBytes(), extName, null);
            imgUrl = fileUrl;
            for (int i = 0; i < upload_file.length; i++) {
                String path = upload_file[i];
                imgUrl += "/" + path;
            }
        }

        System.out.println(imgUrl);
        return imgUrl;
    }

}

```

需要注意的是：不要将fileUrl返回去

利用@Value 标签可以引用application.properties中的值

```java
fileServer.url=http://192.168.67.201
```

测试结果：

![image-20210804120927856](images/image-20210804120927856.png)

至此我们解决了文件上传的功能。

 

### 6注意：

​	application.properties ：fastdfs的路径是有http://

​     测试图片上传的时候，放过自己！

​	tracker.conf！

os7：设置开机启动nginx，fdfs ，centos7.0会失败！

​			解决步骤参见《FastDFS安装说明》

​            或 解决方案：放过自己，手动！首次：/usr/local/nginx/sbin/nginx    重启： /usr/local/nginx/sbin/nginx -s reload

    pid logs/nginx.pid;（把#删除）  
    http://www.cnblogs.com/yufeng218/p/8215381.html
    chmod +x /etc/rc.d/rc.local
    https://www.cnblogs.com/yufeng218/p/8215421.html

==nginx的启动==

ps -A | grep nginx

kill -9 pid1

kill -9 pid2

cd /usr/local/nginx/sbin/

./nginx

# 9、sku保存(spu与sku区别，多表关联查询，mapper写法)



## 1  SPU与SKU

SPU(Standard Product Unit)：标准化产品单元。是商品信息聚合的最小单位，是**一组可复用、易检索的标准化信息的集合**，该集合描述了一个产品的特性。

SKU=Stock Keeping Unit（库存量单位）。即**库存进出计量的基本单元**，可以是以件，盒，托盘等为单位。SKU这是对于大型连锁超市DC（配送中心）物流管理的一个必要的方法。现在已经被引申为产品统一编号的简称，**每种产品均对应有唯一的SKU号**。

比如，咱们购买一台iPhoneX手机，iPhoneX手机就是一个SPU，但是你购买的时候，不可能是以iPhoneX手机为单位买的，商家也不可能以iPhoneX为单位记录库存。必须要以什么颜色什么版本的iPhoneX为单位。比如，你购买的是一台银色、128G内存的、支持联通网络的iPhoneX ，商家也会以这个单位来记录库存数。那这个更细致的单位就叫库存单元（SKU）。

**详细见《day05 sku保存》word文档**

   定义xml的时候，尽量使接口名称跟xml名称保持一致

**baseAttrInfoMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper SYSTEM "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.wgml.itmall.manage.mapper.BaseAttrInfoMapper">
<!--    autoMapping自动映射 相同的类属性名和数据库表字段名-->
    <resultMap id="baseAttrInfoMap" type="com.wgml.itmall.bean.manage.BaseAttrInfo" autoMapping="true">
        <id column="id" property="id"></id>
<!--        一对多-->
        <collection ofType="com.wgml.itmall.bean.manage.BaseAttrValue" property="attrValueList" autoMapping="true">
            <id property="id" column="attr_value_id"></id>
        </collection>
<!--     多对一   <association></association>-->
    </resultMap>
    <select id="getBaseAttrInfoListByCatalog3Id" resultMap="baseAttrInfoMap">
        SELECT
            bai.id,
            bai.attr_name,
            bai.catalog3_id,
            bav.id attr_value_id,
            bav.value_name,
            bav.attr_id
        FROM
            base_attr_info bai
                INNER JOIN base_attr_value bav ON bai.id = bav.attr_id
        WHERE
            catalog3_id = #{0}

    </select>
</mapper>
```

注意：在application.properties 配置文件中添加

```properties
mybatis.mapper-locations=classpath:mapper/*Mapper.xml
mybatis.configuration.mapUnderscoreToCamelCase=true
```

# 10、Thymeleaf

详情见《day06商品详情》word文档          《Spring Boot核心技术-笔记》

## 1快速入门：

项目创建，依赖模块web，Thymeleaf.模板。

### 1.1 所有头文件

  就想Jsp的<%@Page %>一样 ，Thymeleaf的也要引入标签规范。不加这个虽然不影响程序运行，但是你的idea会认不出标签，不方便开发。

```html
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:th="http://www.thymeleaf.org">
```

### 1.2 取出请求域中的值，即取得request.attribute中的值，Model同样可以使用 

```html
<p th:text="${hello}" th:value="${hello}">打底值</p>
```

### 1.3 循环

```html
<table border="1">
    <tr th:each="skuImage:${skuInfo.skuImageList}">
        <td th:text="${skuImage.id}">
        </td>
        <td th:text="${skuImage.imgName}">
        </td>
      </tr>
</table>

<!--测试循环-->
<table>
    <!-- <c:foreach items="${arrayList}" var = "arry">-->
    <tr th:each="arry,stat: ${arrayList}">
        <td th:text="${arry}"></td>
        <td th:text="${stat.index}"></td>
    </tr>
</table>



```

### 1.4 判断  Thymeleaf：判断的时候，数字类型，字符串类型，单引号可以加，也可以不加。

 ```html
 三元运算：
 <td th:text="(${skuImage.id}=='10')?'是10':'不是10'">
 </td>
 If 判断
 <td th:if="${skuImage.id}=='10'" >123456</td>
 
 
 ```



### 1.5 取session中的属性

```html
<div th:text="${session.userName}"> </div>
如果在页面默认没有写级别的话，默认走的request${skuImage.id }
```



### 1.6 引用内嵌页

```xml
<div th:include="itemInner"/> 
```

如果idea编辑器质疑你页面中的元素是否存在

通常不准确，可以去掉验证，在settings->editor->Inspections中关掉验证。

   ![image-20210808194035253](images/image-20210808194035253.png)

![image-20210808194055650](images/image-20210808194055650.png)

### 1.7 th:utext :解析样式

Controller:

```java
request.setAttribute("gname","<span style=\"color:green\">宝强</span>");
```

Th:utext:识别html中的标签

```html
<p th:utext="${gname}">color</p>
```



# 11、回显并锁定销售属性值，点击销售属性值实现一个切换功能

详情见《day06商品详情》word文档



# 12、安装redis

安装Redis ： 

1. 安装依赖  

2. 导入jar包

3. 创建安装目录

4. Make

5. Make install 

6. 修改配置文件

```shell
安装redis 。。

前提：
gcc工具依赖安装
      			
安装gcc 依赖环境 yum install gcc            yum install gcc-c++ -y

安装redis的时候，可能需要 yum install gcc-c++ -y automake autoconf 这个两个依赖

				然后:
				
				1.	解压
					tar -zxvf redis-3.2.8.tar.gz 
				2.	安装路径
					mkdir -p /usr/local/redis
				3.	在解压的目录文件下进行编译
					make
				3.	直接安装
					make install PREFIX=/usr/local/redis   #默认安装目录为：usr/local/bin
				4.	修改配置文件
					cp /root/redis-3.2.8/redis.conf /usr/local/redis/bin/
					61  bind *
					128 deam~ yes
				5.	redis 的客户端工具
				
				6.	进入bin目录 启动redis
					./redis-server redis.conf
				or	./redis-server ./redis.conf
					关闭 shutdown
					
				注：redis 自启动
				vim /etc/rc.d/rc.local
				/usr/local/redis/bin/redis-server /usr/local/redis/bin/redis.conf 
```

详细看《05_JavaEE技术之Redis》

# 13、项目整合redis

详细看《day07 全文搜索》《Redis笔记.docx》

## 1 整合redis到大工程中。

由于redis作为缓存数据库，要被多个项目使用，所以要制作一个通用的工具类，方便工程中的各个模块使用。

而主要使用redis的模块，都是后台服务的模块，xxx-service工程。所以咱们把redis的工具类放到service-util模块中，这样所有的后台服务模块都可以使用redis。

首先引入依赖包

```xml
<!-- https://mvnrepository.com/artifact/redis.clients/jedis -->
<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>2.9.0</version>
</dependency>
```

分别按照之前的方式放到parent模块和service-util的pom文件中。

然后在service-util中创建两个类RedisConfig和RedisUtil

RedisConfig负责在spring容器启动时自动注入，而RedisUtil就是被注入的工具类以供其他模块调用。

Spring boot 项目 推荐使用注解方式来完成配置。

### RedisUtil工具类

```java
package com.wgml.itmall.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author Ye Linfang
 * @date 2021/8/10 10:53
 * @description Redis工具类  另一种在笔记中
 * @since JDK1.8
 */
@Component
public class RedisUtil implements InitializingBean {

    private JedisPool jedisPool;
    @Value("${spring.redis.host:disabled}")
    private String host;
    @Value("${spring.redis.port:6379}")
    private int port;
    @Value("${spring.redis.database:0}")
    private int database;


    public void initJedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 总数
        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒
        jedisPoolConfig.setMaxWaitMillis(10 * 1000);
        // 最少剩余数
        jedisPoolConfig.setMinIdle(10);
        // 如果到最大数，设置等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 在获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);
        // 创建连接池
        jedisPool = new JedisPool(jedisPoolConfig, host, port, 20 * 1000);
    }


    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        initJedisPool();
    }
}

```

另外版

```java
package com.atguigu.gmall.config;

public class RedisUtil {

    private JedisPool jedisPool;

    public  void  initJedisPool(String host,int port,int database){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 总数
        jedisPoolConfig.setMaxTotal(200);
        // 获取连接时等待的最大毫秒
        jedisPoolConfig.setMaxWaitMillis(10*1000);
        // 最少剩余数
        jedisPoolConfig.setMinIdle(10);
        // 如果到最大数，设置等待
        jedisPoolConfig.setBlockWhenExhausted(true);
        // 在获取连接时，检查是否有效
        jedisPoolConfig.setTestOnBorrow(true);
// 创建连接池
jedisPool = new  JedisPool(jedisPoolConfig,host,port,20*1000);

    }
    public Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }
}


======================分割线=========================================================
    
//RedisConfig
//@Configuration 相当于spring3.0版本的xml 

@Configuration
public class RedisConfig {

    //读取配置文件中的redis的ip地址
    @Value("${spring.redis.host:disabled}")
    private String host;

    @Value("${spring.redis.port:0}")
    private int port;

    @Value("${spring.redis.database:0}")
    private int database;

    @Bean
    public RedisUtil getRedisUtil(){
        if(host.equals("disabled")){
            return null;
        }
        RedisUtil redisUtil=new RedisUtil();
        redisUtil.initPool(host,port,database);
        return redisUtil;
    }

}


```

同时，任何模块想要调用redis都必须在application.properties配置，否则不会进行注入。

```properties
spring.redis.host=192.168.67.204
spring.redis.port=6379
spring.redis.database=0
```

现在可以在manage-service中的getSkuInfo()方法测试一下

```java
try {
    Jedis jedis = redisUtil.getJedis();
    jedis.set("test","text_value" );
}catch (JedisConnectionException e){
    e.printStackTrace();
}

//在对应服务模块启动类上加上@ComonentScan注解
@ComponentScan(basePackages = "com.wgml.itmall")  //扫描redis配置所在的包

```

## 2 使用redis进行业务开发

开始开发先说明redis key的命名规范，由于Redis不像数据库表那样有结构，其所有的数据全靠key进行索引，所以redis数据的可读性，全依靠key。

企业中最常用的方式就是：object:skuId:field

​         比如：sku:1314:info

​        		    user:1092:info

**:**表示根据windows的 /一个意思

redis数据类型相关知识点：

```java
	
	// 设计redis ,必须注意使用那种数据类型来存储数据！
        /*
            redis 五种数据类型使用场景！
            String：短信验证码，存储一个变量
            hash：json 字符串{对象转换的字符串}
                hset(key,field,value);
                hset(key,id,1);
                hset(key,name,admin);
                hget(key,field)
            // update bean

            list：lpush ，pop 队列使用
            set：去重，交集，并集，补集。。。 不重复！
            zset：评分，排序
         */
        /*
            hash：json 字符串{对象转换的字符串}
                hset(key,skuName,"cc");
                hset(key,price,1999);
                hset(key,weight,0.66);
                ---- 修改 price
                String price = hget(key,price);
                price=newprice
                hset(key,price,price)

              String
              set(key,value)
               set (key,SkuInfoToString);

               String skuJson = get(key);
               SkuInfo skuInfo = JSON.parseObject(skuJson,skuInfo.class);
                skuInfo.setPrice(newPrice);
               set(key,JSON.toJSONString(skuInfo))

         */


					// 获取缓存中的数据
			//        jedis.exists(key){
			////            jedis.hget(key,id);
			////            jedis.hget(key,name);
			//            // 获取hash 中的所有数据！
			////            jedis.hgetAll();
			////            jedis.hvals();
			//
			//        }
```



重构getSkuInfo方法

在itmall-manage-service项目中定义常量

```java
package com.wgml.itmall.manage.constant;

/**
 * @author Ye Linfang
 * @date 2021/8/10 13:39
 * @description 常量类
 * @since JDK1.8
 */
public class ManageConst {
    public static final String SKUKEY_PREFIX="sku:";

    public static final String SKUKEY_SUFFIX=":info";
    //一天
    public static final int SKUKEY_TIMEOUT=24*60*60;
    //10秒
    public static final int SKULOCK_EXPIRE_PX=10000;
    
	public static final String SKULOCK_SUFFIX=":lock";

}
```

```java
 @Override
    public SkuInfo getSkuInfo(String skuId) {
        SkuInfo skuInfo = null;
        String skuInfokey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            if(jedis.exists(skuInfokey)){
                //存在key
                String skuInfoJsonString = jedis.get(skuInfokey);
                if(!StringUtils.isEmpty(skuInfoJsonString)){
                    //数据不为空
                    skuInfo = JSON.parseObject(skuInfoJsonString,SkuInfo.class);
                    log.debug("缓存查完返回转换数据");
                }
            }else {
                //TODO 考虑缓存key失效缓存击穿问题

                // 不存在key，去DB查询
                 skuInfo = getSkuInfoDB(skuId);
                 //查完放入redis缓存
                jedis.setex(skuInfokey,ManageConst.SKUKEY_TIMEOUT,JSON.toJSONString(skuInfo));
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("redis缓存崩溃，去DB查询skuInfo{}",e.getMessage());
            // redis缓存崩溃，去DB查询
            skuInfo = getSkuInfoDB(skuId);
        } finally {
            if(jedis != null){
                jedis.close();
            }
        }

        return skuInfo;
    }

    private SkuInfo getSkuInfoDB(String skuId) {
        //查询基本信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        //查询属性,设置属性
        skuInfo.setSpuSaleAttrList(getSpuSaleAttrListCheckBySku(skuInfo));
        skuInfo.setSkuSaleAttrValueList(getSkuSaleAttrValueListBySpu(skuInfo.getSpuId()));
        log.debug("数据库DB查询返回SkuInfo数据");
        return skuInfo;
    }

    @Override
    public List<SpuSaleAttr> getSpuSaleAttrListCheckBySku(SkuInfo skuInfo) {
        List<SpuSaleAttr> spuSaleAttrs = spuSaleAttrMapper.selectSpuSaleAttrListCheckBySku(skuInfo.getId(), skuInfo.getSpuId());
        return spuSaleAttrs;
    }

    @Override
    public List<SkuSaleAttrValue> getSkuSaleAttrValueListBySpu(String spuId) {
        return skuSaleAttrValueMapper.selectSkuSaleAttrValueListBySpu(spuId);
    }
```

以上基本实现使用缓存的方案。

 

如果说：redis服务没有启动，宕机！如何解决？

   1、 Try-catch-finally 获取mysql数据返回

   2、高可用集群！

如果说：访问redis的时候，可能会有产生高并发，如何解决这种高并发访问？

​     使用分布式锁！

​     set test ok px 10000 nx

```java
redis 面试常问问题
				缓存击穿
					缓存中的某一个key，失效了。此时会导致大量用户访问数据库。会造成缓存的击穿！
					加锁：
						分布式锁：
						redis 	setnx，setex
								set(key,value,nx,px,timeout)
								set k1 v1 px 10000 nx
								
								使用k1 当作锁！
								
						redisson：分布式锁！
							redisson ： 工具
							github.com
							https://github.com/search?q=redisson
								
				缓存雪崩
					缓存中的所有key全部失效！此时会给数据库造成压力！
					解决方案：
						将缓存中的key 设置的过期时间不一致，不同！
				
				缓存穿透
					用户查询一个在数据库中根本不存在的数据，此时会造成穿透！
					
					if(key){
						// 获取缓存数据 
						get(key);
					}else{
						// 走数据库 getDB();
						//  getDB(); null
						
						set(key,null);
						
					}
```

## 3 解决缓存击穿问题：

Redis:命令  网址：http://doc.redisfans.com/     也可以看《Redis笔记.docx》

set sku:1:info “OK” NX PX 10000

EX second ：设置键的过期时间为 second 秒。 SET key value EX second 效果等同于 SETEX key second value 。

PX millisecond ：设置键的过期时间为 millisecond 毫秒。 SET key value PX millisecond 效果等同于 PSETEX key millisecond value 。

NX ：只在键不存在时，才对键进行设置操作。 SET key value NX 效果等同于 SETNX key value 。

XX ：只在键已经存在时，才对键进行设置操作。

```java
 /**
     * @param skuId
     * @return: com.wgml.itmall.bean.manage.SkuInfo
     * @author Ye Linfang
     * @date 2021/8/10 21:21
     * @description 使用Jedis实现分布式锁解决缓存击穿问题
     */
    private SkuInfo getInfoJedis(String skuId) {
        SkuInfo skuInfo = null;
        // 定义key
        String skuInfoKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
        Jedis jedis = null;

        try {
            jedis = redisUtil.getJedis();
            if (jedis.exists(skuInfoKey)) {
                //存在key 命中缓存
                log.debug("存在skuInfoKey，命中缓存！");
                String skuInfoJsonString = jedis.get(skuInfoKey);
                if (!StringUtils.isEmpty(skuInfoJsonString)) {
                    //数据不为空，返回数据
                    skuInfo = JSON.parseObject(skuInfoJsonString, SkuInfo.class);
                    log.debug("缓存查完返回转换数据{}", skuInfo);
                }
            } else {
                log.debug("不存在skuInfoKey 没有命中缓存！");
                //不存在key 没有命中缓存，需考虑缓存key失效、防止大量用户访问后台数据库导致崩溃，缓存击穿问题
                // 需要加锁！没有拿到锁的就睡眠，直到有锁的取出完数据，还要放入缓存中，下次直接从缓存中取得即可！
                // 1定义key sku:skuId:lock
                String skuLockKey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKULOCK_SUFFIX;
                // 2生成锁 10s   旧版用setnx，setex
                String lockKey = jedis.set(skuLockKey, "OK", "NX", "PX", ManageConst.SKULOCK_EXPIRE_PX);
                // 3判断
                if ("OK".equals(lockKey)) {
                    log.debug("获取锁！,去数据库中取得数据");
                    // 从数据库中取得数据
                    skuInfo = getSkuInfoDB(skuId);
                    // 将对象数据转换成字符串并放入缓存
                    jedis.setex(skuInfoKey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
                } else {
                    log.debug("睡眠等待！");
                    // 等待
                    Thread.sleep(1000);
                    // 自旋
                    skuInfo = getSkuInfo(skuId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            log.debug("redis缓存崩溃，去DB查询skuInfo{}", e.getMessage());
            // redis缓存崩溃，去DB查询
            skuInfo = getSkuInfoDB(skuId);
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return skuInfo;
    }
```

## 4 redisson 解决分布式锁

官网：https://github.com/search?q=redisson

1. 导入依赖 service-util

   ```xml
   <dependency>
      <groupId>org.redisson</groupId>
      <artifactId>redisson</artifactId>
      <version>3.11.1</version>
   </dependency>  
   ```

2. 修改实现类

   ```java
   private SkuInfo getSkuInfoRedisson(String skuId) {
           Config config = new Config();
           config.useSingleServer().setAddress("redis://192.168.67.219:6379");
   
           RedissonClient redissonClient = Redisson.create(config);
   
           // 使用redisson 调用getLock
           RLock lock = redissonClient.getLock("yourLock");
   
   
           // 加锁
           lock.lock(10, TimeUnit.SECONDS);
   		//最多100等待秒数    10秒后释放锁
   //        try {    
   //            boolean res = lock.tryLock(100, 10, TimeUnit.SECONDS);
   //
   //        } catch (InterruptedException e) {
   //            e.printStackTrace();
   //        }
           // 放入业务逻辑代码
           SkuInfo skuInfo =null;
           Jedis jedis = null;
           // ctrl+alt+t
           try {
               jedis = redisUtil.getJedis();
               // 定义key： 见名之意： sku：skuId:info
               String skuKey = ManageConst.SKUKEY_PREFIX+skuId+ManageConst.SKUKEY_SUFFIX;
               // 判断缓存中是否有数据，如果有，从缓存中获取，没有从db获取并将数据放入缓存！
               // 判断redis 中是否有key
               if (jedis.exists(skuKey)){
                   // 取得key 中的value
                   String skuJson = jedis.get(skuKey);
                   // 将字符串转换为对象
                   skuInfo = JSON.parseObject(skuJson, SkuInfo.class);
   //                jedis.close();
                   return skuInfo;
               }else {
                   skuInfo = getSkuInfoDB(skuId);
                   // 放redis 并设置过期时间
                   jedis.setex(skuKey,ManageConst.SKUKEY_TIMEOUT,JSON.toJSONString(skuInfo));
                   return skuInfo;
               }
           } catch (Exception e) {
               e.printStackTrace();
           } finally {
               if (jedis!=null){
                   jedis.close();
               }
               lock.unlock();
           }
           return getSkuInfoDB(skuId);
       }
   
   ```

   ```java
       /**
        * @param skuId
        * @return: com.wgml.itmall.bean.manage.SkuInfo
        * @author Ye Linfang
        * @date 2021/8/10 21:22
        * @description 使用Redission实现分布式锁解决缓存击穿问题
        */
       private SkuInfo getInfoRedission(String skuId) {
           RLock lock = null;
           SkuInfo skuInfo = null;
           String skuInfokey = ManageConst.SKUKEY_PREFIX + skuId + ManageConst.SKUKEY_SUFFIX;
           Jedis jedis = null;
           try {
               Config config = new Config();
               config.useSingleServer().setAddress(ManageConst.REDISSION_HOST);
               RedissonClient redissonClient = Redisson.create(config);
               // 使用redisson 调用getLock
               lock = redissonClient.getLock("yourLock");
               // 加锁
               lock.lock(10, TimeUnit.SECONDS);
               
               // 放入业务逻辑代码
               jedis = redisUtil.getJedis();
               if (jedis.exists(skuInfokey)) {
                   //存在key
                   String skuInfoJsonString = jedis.get(skuInfokey);
                   if (!StringUtils.isEmpty(skuInfoJsonString)) {
                       //数据不为空
                       skuInfo = JSON.parseObject(skuInfoJsonString, SkuInfo.class);
                       log.debug("缓存查完返回转换数据");
                   }
               } else {
                   //考虑缓存key失效缓存击穿问题  这里用redission分布式锁解决了
                   // 不存在key，去DB查询
                   log.debug(" 不存在skuInfoKey，去DB查询");
                   skuInfo = getSkuInfoDB(skuId);
                   //查完放入redis缓存
                   jedis.setex(skuInfokey, ManageConst.SKUKEY_TIMEOUT, JSON.toJSONString(skuInfo));
               }
           } catch (Exception e) {
               e.printStackTrace();
               log.debug("redis缓存崩溃，去DB查询skuInfo{}", e.getMessage());
               // redis缓存崩溃，去DB查询
               skuInfo = getSkuInfoDB(skuId);
           } finally {
               if (jedis != null) {
                   jedis.close();
               }
               //释放锁
               if(lock != null){
                   lock.unlock();
               }
           }
   
           return skuInfo;
       }
   ```

   





# 14、ab工具安装和使用

Redis事务秒杀并发模拟

安装

```shell
#使用工具ab
#CentOS6 默认安装
#CentOS7需要手动安装

#1、联网：
yum install httpd-tools
#2、无网络：
（1） 进入cd  /run/media/root/CentOS 7 x86_64/Packages（路径跟centos6不同）
（2） 顺序安装
     rpm -ivh apr-1.4.8-3.el7.x86_64.rpm
     rpm -ivh apr-util-1.5.2-6.el7.x86_64.rpm
     rpm -ivh httpd-tools-2.4.6-67.el7.centos.x86_64.rpm 
```

使用

```shell
ab –n 请求数  -c 并发数  -p 指定请求数据参数文件 -T  “application/x-www-form-urlencoded” 测试的请求
#例子	
ab -n 1000  -c 200  -p /opt/postfile -T  "application/x-www-form-urlencoded" 192.168.43.85:8080/Seckill/doseckill

ab -n 100  -c 10 -T  "application/x-www-form-urlencoded" 192.168.1.6:8504/34.html
```



# 15、ElasticSearch和Kibana

## 1  安装包下载

Elasticsearch官网： https://www.elastic.co/products/elasticsearch

https://www.elastic.co/downloads/past-releases/elasticsearch-5-6-4

本课程选择的版本是elasticsearch-5.6.4   

==安装包资料在：《D:\桌面文件库\java资料\电商\资料\day 07》==

![image-20210811131722629](images/image-20210811131722629.png)

下载好后放到/opt/目录下

## 2  安装elasticsearch

拷贝elasticsearch-5.6.4.rpm到/opt目录下

![image-20210811131813646](images/image-20210811131813646.png)

### 2.1  注册并启动服务

![image-20210811131848297](images/image-20210811131848297.png)

CentOS6.8 通过chkconfig --list可以查看

![image-20210811131922047](images/image-20210811131922047.png)

 CentOS7.x 可以通过下面语句注册服务

systemctl list-unit-files|grep elasticsearch

systemctl enable elasticsearch.service

![image-20210811131938368](images/image-20210811131938368.png)

启动之前为elasticsearch配置jdk

vim /etc/sysconfig/elasticsearch 中修改JAVA_HOME路径的路径

![image-20210811131953410](images/image-20210811131953410.png)

JAVA_HOME=/opt/jdk1.8.0_152启动elasticsearch

![image-20210811132022437](images/image-20210811132022437.png)

查看进程

![image-20210811132039202](images/image-20210811132039202.png)

核心文件

```shell
vim /etc/elasticsearch/elasticsearch.yml
数据文件路径
/var/lib/elasticsearch/
日志文件路径
/var/log/elasticsearch/elasticsearch.log
```

### 2.2  修改配置文件

```shell
 vim  /etc/elasticsearch/elasticsearch.yml  
```

修改yml配置的注意事项:

每行必须顶格，不能有空格

“：”后面必须有一个空格

 

集群名称，同一集群名称必须相同

![image-20210811132208151](images/image-20210811132208151.png)

单个节点名称 ![image-20210811132225544](images/image-20210811132225544.png)

网络部分 改为当前的ip地址 ，端口号保持默认9200就行

![image-20210811132239763](images/image-20210811132239763.png)

把bootstrap自检程序关掉

bootstrap.system_call_filter: false

![image-20210811132254054](images/image-20210811132254054.png)

自发现配置：新节点向集群报到的主机名，也可以写ip地址

![image-20210811132305641](images/image-20210811132305641.png)

### 2.3  修改linux配置

为什么要修改linux配置？

默认elasticsearch是单机访问模式，就是只能自己访问自己。

但是我们之后一定会设置成允许应用服务器通过网络方式访问。这时，elasticsearch就会因为嫌弃单机版的低端默认配置而报错，甚至无法启动。

所以我们在这里就要把服务器的一些限制打开，能支持更多并发。

 **问题**1：max file descriptors [4096] for elasticsearch process likely too low, increase to at least [65536] elasticsearch

原因：系统允许 Elasticsearch 打开的最大文件数需要修改成65536

解决：vi /etc/security/limits.conf

添加内容：

```shell
* soft nofile 65536
* hard nofile 131072
* soft nproc 2048
* hard nproc 65536
```

注意：“*” 不要省略掉

 

**问题**2：max number of threads [1024] for user [judy2] likely too low, increase to at least [2048] （==CentOS7.x 不用改==）

 

原因：允许最大进程数修该成2048

解决：vi /etc/security/limits.d/90-nproc.conf  

修改如下内容：

\* soft nproc 1024

\#修改为

 \* soft nproc 2048

\* soft nproc 4096

 

 

**问题**3：max virtual memory areas vm.max_map_count [65530] likely too low, increase to at least [262144] （==CentOS7.x 不用改==）

原因：一个进程可以拥有的虚拟内存区域的数量。

解决：可临时提高vm.max_map_count的大小

命令：sysctl -w vm.max_map_count=262144

### 2.4  重启linux 

vim /etc/elasticsearch/jvm.options

调整一下elasticsearch 的配置，将启动的内存调整为512m

 reboot  重启电脑

启动elasticsearch

 service elasticsearch start 

### 2.5  测试

```shell
[root@centos147 ~]# curl http://192.168.72.139:9200
{
  "name" : "atguigu",
  "cluster_name" : "my-application",
  "cluster_uuid" : "dLWGNLJsQ7efpvofNCQLlA",
  "version" : {
    "number" : "5.6.4",
    "build_hash" : "8bbedf5",
    "build_date" : "2017-10-31T18:55:38.105Z",
    "build_snapshot" : false,
    "lucene_version" : "6.6.1"
  },
  "tagline" : "You Know, for Search"
}
```

或者直接浏览器访问http://192.168.67.147:9200

![image-20210811132536153](images/image-20210811132536153.png)

### 2.6  如果启动未成功

如果启动未成功，请去查看相关日志

vim  /var/log/elasticsearch/my-es.log

## 3  安装kibana

拷贝kibana-5.6.4-linux-x86_64.tar 到/opt下

解压缩

进入kibana主目录的config目录下

![image-20210811132641652](images/image-20210811132641652.png)

[root@localhost config]# pwd

/opt/kibana-5.6.4-linux-x86_64/config

[root@localhost config]# vim kibana.yml

![image-20210811132705690](images/image-20210811132705690.png)

启动

 在 kibana主目录bin目录下执行

nohup  ./kibana  &

然后ctrl+c退出

执行ps -ef 

![image-20210811132717680](images/image-20210811132717680.png)

如上图,1757号进程就是kibana的进程

 

用浏览器打开

http://192.168.xx.xx:5601/

![image-20210811132731760](images/image-20210811132731760.png)

点击左边菜单DevTools

在Console中 

执行 get _cluster/health  

右边的结果中，status为yellow或者green。

表示es启动正常，并且与kibana连接正常。

## 4 ==补充：更多es的知识和dsl语法请看《day07 全文搜索.word》《es 补充知识.txt》《day 07.txt》《java高级文件夹》==

```shell
全文检索：
		为什么使用全文检索：
			SELECT * FROM sku_info si WHERE si.sku_name like '%手机%';  慢！索引失效！
		为了提高检索效率：采用专业的企业级搜索引擎！
			es - solr 倒排索引！
		请问用谁？
			es ：快！在数据节点检索过程中，如果有动态实时添加的数据节点情况下。检索贼快！
			solr：慢！如果有动态实时添加的数据节点情况下。检索贼慢！ 在数据节点保持不变的情况下，检索效率快！
				solr：本质 solr.war 动态项目工程！
		
		了解：
			基本知识：
				INDEX: database
				TYPE: table
				DOUCMENT: row
				FIELD: column
				
				es 的 中文词库！
				
				自定义词库：热词，流行词
				
			
			基本命令：
				GET: 查询
				PUT: 添加
				DELETE: 删除
				POST: 修改

				dsl：命令
				高亮：
				分页：
				过滤：
				查询：
				聚合：
				排序：
	---------------------------------
		ELK:
			E:elstaticsearch  全文检索
			L:logstash  收集日志
			K:kibana   操作es命令，可视化界面操作！
		
		安装：
			EK:
		
		
		制作：首页
			www.gmall.com
			
			1.	创建文件夹
			
			2.	导入静态资源
			
			3.	配置nginx.conf
				server {
				  listen       80;
				  server_name  www.gmall.com;    # ----->host里面跳转解析为虚拟机ip地址 
				  location / {
						root front;
						index index.htm;
						}
				}
				
				重启nginx
				
			4.	修改hosts 文件  修改工具SwitchHosts.exe在《D:\桌面文件库\java资料\电商\资料\day 01》文件夹下
				192.168.67.219 www.gmall.com
		
			5.	追加
				// 代理域名 item.gmall.com
				
				server 192.168.67.1:8084;
				192.168.67.1 ：VMNET8
				ip:每天都会变 本地ip地址变为固定！
				upstream item.gmall.com{
						server 192.168.67.1:8084;
					}

				server {
				  listen       80;
				  server_name  item.gmall.com;
				  location / {
						proxy_pass http://item.gmall.com;
						}
				}
			
              6.重启nginx   sbin目录下 ./nginx -s reload
   
```

## 5 elasticsearch的基本概念

| cluster      | 整个elasticsearch 默认就是集群状态，整个集群是一份完整、互备的数据。 |
| ------------ | ------------------------------------------------------------ |
| node         | 集群中的一个节点，一般只一个进程就是一个node                 |
| Shard        | 分片，即使是一个节点中的数据也会通过hash算法，分成多个片存放，默认是5片。 |
| **Index**    | 相当于rdbms的database, 对于用户来说是一个逻辑数据库，虽然物理上会被分多个shard存放，也可能存放在多个node中。 |
| **Type**     | 类似于rdbms的table，但是与其说像table，其实更像面向对象中的class , 同一Json的格式的数据集合。 |
| **Document** | 类似于rdbms的 row、面向对象里的object                        |
| **Field**    | 相当于字段、属性                                             |

## 6 利用kibana学习 elasticsearch restful api (DSL)

### 6.1  es中保存的数据结构

```java
public class  Movie {
	 String id;
     String name;
     Double doubanScore;
     List<Actor> actorList;
}

public class Actor{
String id;
String name;
}
```

这两个对象如果放在关系型数据库保存，会被拆成2张表，但是elasticsearch是用一个json来表示一个document。

所以它保存到es中应该是：

```json
{
  “id”:”1”,
  “name”:”operation red sea”,
  “doubanScore”:”8.5”,
  “actorList”:[  
    {“id”:”1”,”name”:”zhangyi”},
    {“id”:”2”,”name”:”haiqing”},
    {“id”:”3”,”name”:”zhanghanyu”}
  ]
}
```

### 6.2对数据的操作

```java
GET _search
{
  "query": {
    "match_all": {}
  }
}

GET _cluster/health

//查看es中有哪些索引
GET /_cat/indices?v
/*    
es 中会默认存在一个名为.kibana的索引
表头的含义
health	green(集群完整) yellow(单点正常、集群不完整) red(单点不正常)
status	是否能使用
index	索引名
uuid	索引统一编号         
pri	主节点几个
rep	从节点几个
docs.count	文档数
docs.deleted	文档被删了多少
store.size	整体占空间大小
pri.store.size	主节点占
*/

    
//增加一个索引    
PUT /movie_index
//删除一个索引
//ES 是不删除也不修改任何数据 ,伪删除更新当前index的版本。
DELETE /movie_index
    
    
//新增文档 
//1、	格式 PUT /index/type/id    
PUT /movie_index/movie/1
{ "id":1,
  "name":"operation red sea",
  "doubanScore":8.5,
  "actorList":[  
{"id":1,"name":"zhang yi"},
{"id":2,"name":"hai qing"},
{"id":3,"name":"zhang han yu"}
]
}
PUT /movie_index/movie/2
{
  "id":2,
  "name":"operation meigong river",
  "doubanScore":8.0,
  "actorList":[  
{"id":3,"name":"zhang han yu"}
]
}

PUT /movie_index/movie/3
{
  "id":3,
  "name":"incident red sea",
  "doubanScore":5.0,
  "actorList":[  
{"id":4,"name":"liu de hua"}
]
}
//如果之前没建过index或者type，es 会自动创建。
    
//直接用id查找    
GET movie_index/movie/1

//修改—整体替换 和新增没有区别
PUT /movie_index/movie/3
{
  "id":"3",
  "name":"incident red sea",
  "doubanScore":"5.0",
  "actorList":[  
{"id":"1","name":"zhang guo li"}
]
}

//修改—某个字段 更新es商品中的排名
POST movie_index/movie/3/_update
{ 
  "doc": {
    "doubanScore":"7.0"
  } 
}

//删除一个document
DELETE movie_index/movie/3
//搜索type全部数据
GET movie_index/movie/_search
//结果
 {
  "took": 2,    //耗费时间 毫秒
  "timed_out": false, //是否超时
  "_shards": {
    "total": 5,   //发送给全部5个分片
    "successful": 5,
    "skipped": 0,
    "failed": 0
  },
  "hits": {
    "total": 3,  //命中3条数据
    "max_score": 1,   //最大评分
    "hits": [  // 结果
      {
        "_index": "movie_index",
        "_type": "movie",
        "_id": 2,
        "_score": 1,
        "_source": {
          "id": "2",
          "name": "operation meigong river",
          "doubanScore": 8.0,
          "actorList": [
            {
              "id": "1",
              "name": "zhang han yu"
            }
          ]
        }
          。。。。。。。。
          。。。。。。。。
      }
   
//按条件查询(全部)
GET movie_index/movie/_search
{
  "query":{
    "match_all": {}
  }
}



//按分词查询  注意结果的评分
GET movie_index/movie/_search
{
  "query":{
    "match": {"name":"red"}
  }
}


//按分词子属性查询
GET movie_index/movie/_search
{
  "query":{
    "match": {"actorList.name":"zhang"}
  }
}



//match phrase 按词组查询   按短语查询，不再利用分词技术，直接用短语在原始数据中匹配
GET movie_index/movie/_search
{
    "query":{
      "match_phrase": {"name":"operation red"}
    }
}

// fuzzy查询  校正匹配分词，当一个单词都无法准确匹配，es通过一种算法对非常接近的单词也给与一定的评分，能够查询出来，但是消耗更多的性能。
GET movie_index/movie/_search
{
    "query":{
      "fuzzy": {"name":"rad"}
    }
}

// 过滤--查询后过滤
GET movie_index/movie/_search
{
    "query":{
      "match": {"name":"red"}
    },
    "post_filter":{
      "term": {
        "actorList.id": 3
      }
    }
}


//过滤--查询前过滤（推荐）
        其实准确来说，ES中的查询操作分为2种：查询（query）和过滤（filter）。查询即是之前提到的query查询，它（查询）默认会计算每个返回文档的得分，然后根据得分排序。而过滤（filter）只会筛选出符合的文档，并不计算得分，且它可以缓存文档。所以，单从性能考虑，过滤比查询更快。
换句话说，过滤适合在大范围筛选数据，而查询则适合精确匹配数据。一般应用时，应先使用过滤操作过滤数据，然后使用查询匹配数据。

term、terms过滤
term、terms的含义与查询时一致。term用于精确匹配、terms用于多词条匹配。不过既然过滤器适用于大氛围过滤，term、terms在过滤中使用意义不大。在项目中建议使用term。

Term: where id = ?
Terms: where id in ()
# select * from skuInfo where id=?  Select * from skuInfo where id in ()
        
        
GET movie_index/movie/_search
{ 
    "query":{
        "bool":{
          "filter":[ {"term": {  "actorList.id": "1"  }},
                     {"term": {  "actorList.id": "3"  }}
           ], 
           "must":{"match":{"name":"red"}}
         }
    }
}


//过滤--按范围过滤
//关于范围操作符：跟html标签中的转义字符一样！
gt	大于
lt	小于
gte	大于等于
lte	小于等于

GET movie_index/movie/_search
{
   "query": {
     "bool": {
       "filter": {
         "range": {
            "doubanScore": {"gte": 8}
         }
       }
     }
   }
}

//排序
面试题：
Mysql 默认升序
Oracle 默认是升序
Sqlserver 默认是升序
端口号不一样，分页语句不一样！
3306 1521 1433
Limit rownum top
        
GET movie_index/movie/_search
{
  "query":{
    "match": {"name":"red sea"}
  }
  , "sort": [
    {
      "doubanScore": {
        "order": "desc"
      }
    }
  ]
}

//分页查询
GET movie_index/movie/_search
{
  "query": { "match_all": {} },
// 第几条开始查询！第几条开始=（第几页-1）*每页条数
  "from": 0,
  //每页条数
  "size": 1
}
//指定查询的字段
GET movie_index/movie/_search
{
  "query": { "match_all": {} },
  "_source": ["name", "doubanScore"]
}
        
//高亮
GET movie_index/movie/_search
{
    "query":{
      "match": {"name":"red sea"}
    },
    "highlight": {
      "fields": {"name":{} }
    }
    
}

//修改自定义高亮标签
GET movie_index/movie/_search
{
    "query":{
      "match": {"name":"red sea"}
    },
    "highlight": {
      "post_tags": ["</span>"],
      "pre_tags": ["<span style=color:red>"], 
      "fields": {"name":{} }
    }
}
// 聚合 取出每个演员共参演了多少部电影 –  sql  ： group by         
GET movie_index/movie/_search
{ 
  "aggs": {
    "groupby_actor": {
      "terms": {
        "field": "actorList.name.keyword"  
      }
    } 
  }
}
//每个演员参演电影的平均分是多少，并按评分排序
GET movie_index/movie/_search
{ 
  "aggs": {
    "groupby_actor_id": {
      "terms": {
        "field": "actorList.name.keyword" ,
        "order": {
          "avg_score": "desc"
          }
      },
      "aggs": {
        "avg_score":{
          "avg": {
            "field": "doubanScore" 
          }
        }
       }
    } 
  }
}
        
        
GET movie_index/_analyze
{  
  "text": "我是中国人"
}
GET movie_index/_analyze
{  "analyzer": "ik_smart", 
  "text": "我是中国人"
}
PUT movie_chn
{
  "mappings": {
    "movie":{
      "properties": {
        "id":{
          "type": "long"
        },
        "name":{
          "type": "text"
          , "analyzer": "ik_smart"
        },
        "doubanScore":{
          "type": "double"
        },
        "actorList":{
          "properties": {
            "id":{
              "type":"long"
            },
            "name":{
              "type":"keyword"
            }
          }
        }
      }
    }
  }
}
PUT /movie_chn/movie/1
{ "id":1,
  "name":"红海行动",
  "doubanScore":8.5,
  "actorList":[  
  {"id":1,"name":"张译"},
  {"id":2,"name":"海清"},
  {"id":3,"name":"张涵予"}
 ]
}
PUT /movie_chn/movie/2
{
  "id":2,
  "name":"湄公河行动",
  "doubanScore":8.0,
  "actorList":[  
{"id":3,"name":"张涵予"}
]
}

PUT /movie_chn/movie/3
{
  "id":3,
  "name":"红海事件",
  "doubanScore":5.0,
  "actorList":[  
{"id":4,"name":"张国立"}
]
}
GET /movie_chn/movie/_search
{
  "query": {
    "match": {
      "name": "红海战役"
    }
  }
}

GET /movie_chn/movie/_search
{
  "query": {
    "term": {
      "actorList.name": "张译"
    }
  }
}

```

### 6.3关于mapping

之前说type可以理解为table，那每个字段的数据类型是如何定义的呢

查看看mapping

```java
GET movie_index/_mapping/movie
    
实际上每个type中的字段是什么数据类型，由mapping定义。
但是如果没有设定mapping系统会自动，根据一条数据的格式来推断出应该的数据格式。
	true/false → boolean
	1020  →  long
	20.1 → double，float
	“2018-02-01” → date
	“hello world” → text +keyword
默认只有text会进行分词，keyword是不会分词的字符串。
mapping除了自动定义，还可以手动定义，但是只能对新加的、没有数据的字段进行定义。一旦有了数据就无法再做修改了。
注意：虽然每个Field的数据放在不同的type下,但是同一个名字的Field在一个index下只能有一种mapping定义。
    
```











## 7安装中文分词

elasticsearch本身自带的中文分词，就是单纯把中文一个字一个字的分开，根本没有词汇的概念。但是实际应用中，用户都是以词汇为条件，进行查询匹配的，如果能够把文章以词汇为单位切分开，那么与用户的查询条件能够更贴切的匹配上，查询速度也更加快速。

分词器下载网址：https://github.com/medcl/elasticsearch-analysis-ik

 

#### 1 安装

下载好的zip包，请解压后放到 /usr/share/elasticsearch/plugins/

 [root@localhost plugins]# unzip elasticsearch-analysis-ik-5.6.4.zip

![image-20210811181128149](images/image-20210811181128149.png)

==将压缩包文件删除！否则启动失败！==

然后重启es

[root@localhost plugins]# service elasticsearch restart

#### 2 测试使用

使用默认

```shell
GET movie_index/_analyze
{  
  "text": "我是中国人"
}
```

 请观察结果

 使用分词器

```shell
GET movie_index/_analyze
{  "analyzer": "ik_smart", 
  "text": "我是中国人"
}
```

请观察结果

  另外一个分词器

  ik_max_word

```shell
GET movie_index/_analyze
{  "analyzer": "ik_max_word", 
  "text": "我是中国人"
}
```

请观察结果

能够看出不同的分词器，分词有明显的区别，所以以后定义一个type不能再使用默认的mapping了，要手工建立mapping, 因为要选择分词器。

==补充：更多基于中文分词搭建索引和自定义词库请查看《07day 全文检索》word文档==

```java
基于中文分词搭建索引
    建立mapping
    PUT movie_chn
{
  "mappings": {
    "movie":{
      "properties": {
        "id":{
          "type": "long"
        },
        "name":{
          "type": "text"
          , "analyzer": "ik_smart"
        },
        "doubanScore":{
          "type": "double"
        },
        "actorList":{
          "properties": {
            "id":{
              "type":"long"
            },
            "name":{
              "type":"keyword"
            }
          }
        }
      }
    }
  }
}
插入数据
    PUT /movie_chn/movie/1
{ "id":1,
  "name":"红海行动",
  "doubanScore":8.5,
  "actorList":[  
  {"id":1,"name":"张译"},
  {"id":2,"name":"海清"},
  {"id":3,"name":"张涵予"}
 ]
}
PUT /movie_chn/movie/2
{
  "id":2,
  "name":"湄公河行动",
  "doubanScore":8.0,
  "actorList":[  
{"id":3,"name":"张涵予"}
]
}

PUT /movie_chn/movie/3
{
  "id":3,
  "name":"红海事件",
  "doubanScore":5.0,
  "actorList":[  
{"id":4,"name":"张国立"}
]
}
查询测试
    GET /movie_chn/movie/_search
{
  "query": {
    "match": {
      "name": "红海战役"
    }
  }
}

GET /movie_chn/movie/_search
{
  "query": {
    "term": {
      "actorList.name": "张译"
    }
  }
}

```

#### 3 自定义词库

```java
什么使用？
		当词库满足不了你的需要，可以使用自定义词库！
修改/usr/share/elasticsearch/plugins/ik/config/中的IKAnalyzer.cfg.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE properties SYSTEM "http://java.sun.com/dtd/properties.dtd">
<properties>
        <comment>IK Analyzer 扩展配置</comment>
        <!--用户可以在这里配置自己的扩展字典 -->
        <entry key="ext_dict"></entry>
         <!--用户可以在这里配置自己的扩展停止词字典-->
        <entry key="ext_stopwords"></entry>
        <!--用户可以在这里配置远程扩展字典 -->
         <entry key="remote_ext_dict">http://192.168.67.163/fenci/myword.txt</entry>
        <!--用户可以在这里配置远程扩展停止词字典-->
        <!-- <entry key="remote_ext_stopwords">words_location</entry> -->
</properties>

按照标红的路径利用nginx发布静态资源
在nginx.conf中配置
  server {
        listen  80;
        server_name  192.168.67.163;
        location /fenci/ {
           root es;
    }
   }
并且在/usr/local/nginx/下建/es/fenci/目录，目录下加myword.txt
myword.txt中编写关键词，每一行代表一个词。
   下面第一张图
然后重启es服务器，重启nginx。
在kibana中测试分词效果
	下面第二张图
更新完成后，es只会对新增的数据用新词分词。历史数据是不会重新分词的。如果想要历史数据重新分词。需要执行：
    POST movies_index_chn/_update_by_query?conflicts=proceed
```

![image-20210812230809765](images/image-20210812230809765.png)

![image-20210812230816368](images/image-20210812230816368.png)

## 8 java代码实现es操作

## 8.1利用elasticSearch开发电商的搜索列表功能

1. 搭建项目

2. pom.xml导入jest依赖

   ```xml
   <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
   </dependency>
   
   <!-- https://mvnrepository.com/artifact/io.searchbox/jest -->
   <dependency>
      <groupId>io.searchbox</groupId>
      <artifactId>jest</artifactId>
   </dependency>
   
   <!-- https://mvnrepository.com/artifact/net.java.dev.jna/jna -->
   <dependency>
      <groupId>net.java.dev.jna</groupId>
      <artifactId>jna</artifactId>
    </dependency>
   
   <jest.version>5.3.3</jest.version>
   <jna.version>4.5.1</jna.version>
   
   其中jest和jna请将版本号，部分纳入gmall-parent中管理。spring-boot-starter-data-elasticsearch不用管理版本号，其版本跟随springboot的1.5.21大版本号。
   ```
### 1 关于es 的java 客户端的选择

```java
   目前市面上有两类客户端

   一类是TransportClient 为代表的ES原生客户端，不能执行原生dsl语句必须使用它的Java api方法。

   另外一种是以Rest Api为主的missing client，最典型的就是jest。 这种客户端可以直接使用dsl语句拼成的字符串，直接传给服务端，然后返回json字符串再解析。

   两种方式各有优劣，但是最近elasticsearch官网，宣布计划在7.0以后的版本中废除TransportClient。以RestClient为主。

```

![image-20210812223316655](images/image-20210812223316655.png)

 所以在官方的RestClient 基础上，进行了简单包装的Jest客户端，就成了首选，而且该客户端也与springboot完美集成。

### 2 在测试类中测试ES

application.properties中加入

```properties
server.port=8505
logging.level.root=error
spring.dubbo.application.name=list-service
spring.dubbo.registry.protocol=zookeeper
spring.dubbo.registry.address=192.168.72.139:2181
spring.dubbo.base-package=com.wgml.itmall
spring.dubbo.protocol.name=dubbo
spring.datasource.url=jdbc:mysql://localhost:3306/itaomall?characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
#mybatis
mybatis.mapper-locations=classpath:mapper/*Mapper.xml
mybatis.configuration.mapUnderscoreToCamelCase=true

# redis
spring.redis.host=192.168.72.139
spring.redis.port=6379
spring.redis.database=0

spring.elasticsearch.jest.uris=http://192.168.72.139:9200
```

在springBoot 测试类 中添加

```java
package com.wgml.itmall.list;

import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItmallListServiceApplicationTests {

    @Autowired
    JestClient jestClient;
    @Test
    public void contextLoads() {
    }
    @Test
    public void testEsDsl() throws Exception{
        String query = "{\n" +
                "  \"query\": {\n" +
                "    \"term\": {\n" +
                "      \"actorList.name\": \"张译\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(query).addIndex("movie_chn").addType("movie").build();
        SearchResult result = jestClient.execute(search);
        List<SearchResult.Hit<HashMap, Void>> hits = result.getHits(HashMap.class);
        for (SearchResult.Hit<HashMap, Void> hit : hits) {
            HashMap source = hit.source;
            System.err.println(source);
        }

    }

}
```

打印结果：

![image-20210812223614255](images/image-20210812223614255.png)

### 3 根据业务搭建数据结构/建立mapping

建立mapping！

这时我们要思考三个问题：

1、 哪些字段需要分词

​	a)    例如：商品名称[不desc是skuName]

2、 我们用哪些字段进行过滤

​	a)    平台属性[三级分类Id]【真正的过滤应该是通过平台属性值进行过滤】

3、 哪些字段我们需要通过搜索查询出来。

​	a)    商品名称,价格等。

| 需要分词的字段         | **名称**                                       | 分词           |
| ---------------------- | ---------------------------------------------- | -------------- |
| **需要用于过滤的字段** | **三级分类、平台属性值**                       | **不分词**     |
| **需要查询的字段**     | **Sku_id,价格,==名称(关键词高亮)==，图片地址** | **显示的内容** |

以上分析的所有显示，以及分词，过滤的字段都应该在es中出现。Es中如何保存这些数据呢？

“根据上述的字段描述，应该建立一个mappings对应的存上上述字段描述的信息！”

根据以上制定出如下结构：mappings

Index：itaomall

type：SkuInfo

document: properties - rows

field: id,price,skuName…

Es中index默认是true。 该字段是否用来检索

SkuInfo = Type

```json
PUT itaomall
{
  "mappings": {
    "SkuInfo":{
      "properties": {
        "id":{
          "type": "keyword"
          , "index": false
        },
        "price":{
          "type": "double"
        },
         "skuName":{
          "type": "text",
          "analyzer": "ik_max_word"
        },
        "catalog3Id":{
          "type": "keyword"
        },
        "skuDefaultImg":{
          "type": "keyword",
          "index": false
        },
        "skuAttrValueList":{
          "properties": {
            "valueId":{
              "type":"keyword"
            }
          }
        }
      }
    }
  }
}

//版本二
PUT itaomall
{
  "mappings": {
    "SkuInfo":{
      "properties": {
        "id":{
          "type": "keyword"
          , "index": false
        },
        "price":{
          "type": "double"
        },
         "skuName":{
          "type": "text",
          "analyzer": "ik_max_word"
        },
        "catalog3Id":{
          "type": "keyword"
        },
        "skuDefaultImg":{
          "type": "keyword",
          "index": false
        },
        "skuAttrValueList":{
          "properties": {
            "valueId":{
              "type":"keyword"
            }
          }
        },
        "valuesSkuJson":{
          "type": "keyword"
        }
      }
    }
  }
}
//删除索引库
DELETE itaomall

//结果：
{
  "acknowledged": true,
  "shards_acknowledged": true,
  "index": "itaomall"
}
```

注意：ik_max_word 中文词库必须有！

skuAttrValueList：平台属性值的集合，主要用于平台属性值过滤。

### 4 sku数据保存到ES/上架

思路：

回顾一下，es数据保存的dsl javaBean == json格式的数据。

```json
PUT /movie_index/movie/1
{ "id":1,
  "name":"operation red sea",
  "doubanScore":8.5,
  "actorList":[  
  {"id":1,"name":"zhang yi"},
  {"id":2,"name":"hai qing"},
  {"id":3,"name":"zhang han yu"}
 ]
}
```

es存储数据是以json格式保存的，那么如果一个javabean的结构刚好跟要求的json格式吻合，我们就可以直接把javaBean序列化为json保持到es中，所以我们要制作一个与es中json格式一致的javabean.

把es中所有的字段封装到skuLsInfo中

**1 JavaBean**

```java
public class SkuLsInfo implements Serializable {

    String id;

    BigDecimal price;

    String skuName;

    String catalog3Id;

    String skuDefaultImg;

    Long hotScore=0L;

    List<SkuLsAttrValue> skuAttrValueList;
}
```

SkuLsAttrValue

```java
public class SkuLsAttrValue implements Serializable {

    String valueId;
}
```

添加get，set方法

**2 保存sku数据的业务实现类**

自行添加itmall-interface中增加接口方法

```java
public interface ListService {
     Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo);
}
```

在itmall-list-service模块中增加业务实现类listServiceImpl 

```java
package com.wgml.itmall.list.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.wgml.itmall.bean.list.SkuLsInfo;
import com.wgml.itmall.service.list.ListService;
import io.searchbox.client.JestClient;
import io.searchbox.core.DocumentResult;
import io.searchbox.core.Index;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

/**
 * @author Ye Linfang
 * @date 2021/8/13 13:47
 * @description TODO
 * @since JDK1.8
 */
@Service
public class ListServiceImpl implements ListService {
    @Autowired
    private JestClient jestClient;
    public static final String ES_INDEX = "itaomall";
    public static final String ES_TYPE = "SkuInfo";

    @Override
    public Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        // 保存数据
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult documentResult = jestClient.execute(index);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

//检查商品是否上架
 @Override
    public Boolean checkOnSale(String skuId) {
        //创建查询条件
        String query = new SearchSourceBuilder()
                .query(QueryBuilders.matchQuery("_id", skuId))
                .toString();
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
            if (result.getTotal() == 1) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
```

电商业务：分为前台，后台管理

前台：显示，以及购买流程

后台：商品的管理

​     管理：spu，sku，商品的上架，下架。

​         下架：实际就是从es中删除。

​         上架：将最新的产品新增到es上。

​     苹果3,4,4s,5,5s,5c,se,6,6s…

### 5 在后台管理web模块的sku保存中，调用该方法

```java
 @RequestMapping("/onSale")
    @ResponseBody
    public BaseResponse onSale(@RequestParam String skuId){
        //检查是否已经上架
        Boolean checkOnSale = listService.checkOnSale(skuId);
        //上架过了直接返回
        if(checkOnSale == true) {
            return BaseResponse.okBuilder().message("该商品已上架").build();
        }
        //上架 把数据库中的数据保存到es
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        SkuInfo skuInfoDB = manageService.getSkuInfo(skuId); //先检查是否去mysql数据库还是缓存中拿
        BeanUtils.copyProperties(skuInfoDB,skuLsInfo);
        Boolean flat = listService.saveSkuLsInfo(skuLsInfo);
        if(flat == true){
            BaseResponse b = BaseResponse.okBuilder().message("上架成功").build();
            System.out.println(b);
            return b;
        }
        BaseResponse b = BaseResponse.errBuilder().message("上架失败").build();
        System.out.println(b);
        return b;
    }
```

```java
//从数据库中获取数据 
@Override
    public SkuInfo getSkuInfoDB(String skuId) {
        //查询基本信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        skuInfo.setSkuImageList(skuImageList);
        //查询销售属性,设置属性
        //详情页回显锁定
        skuInfo.setSpuSaleAttrList(getSpuSaleAttrListCheckBySku(skuInfo));
        //详情页切换销售属性
        skuInfo.setSkuSaleAttrValueList(getSkuSaleAttrValueListBySpu(skuInfo.getSpuId()));
        //查询平台属性值集合
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        skuInfo.setSkuAttrValueList(skuAttrValueMapper.select(skuAttrValue));
        log.debug("数据库DB查询返回SkuInfo数据");
        return skuInfo;
    }
```

测试：http://localhost:8502/onSale?skuId=33

查询：GET gmall/SkuInfo/3

```json
{
  "_index": "itaomall",
  "_type": "SkuInfo",
  "_id": "33",
  "_version": 8,
  "found": true,
  "_source": {
    "id": "33",
    "price": 2999,
    "skuName": "锤子手机第一代2022款",
    "catalog3Id": "61",
    "skuDefaultImg": "http://192.168.72.139/group1/M00/00/00/wKhIi2EQlMCAEIsGAAIXc1LGT24458.jpg",
    "hotScore": 0,
    "skuAttrValueList": [
      {
        "id": "65",
        "attrId": "23",
        "valueId": "165",
        "skuId": "33"
      },
      {
        "id": "66",
        "attrId": "24",
        "valueId": "159",
        "skuId": "33"
      }
    ]
  }
}
```

### 6 java查询es数据的后台方法

分析：

首先先观察功能页面，咱们一共要用什么查询条件，查询出什么数据？

查询条件： 

1、 关键字

2、 可以通过==分类ID==进入列表页面

3、 平台属性值:过滤

4、 分页页码

查询结果： 

1 sku的列表(关键字高亮显示)

2    这些sku涉及了哪些属性和属性值

3    命中个数[total]，用于分页

  基于以上

#### 1 编写DSL语句 ：

主要目的：是从es中取得数据！

```json
GET /itaomall/SkuInfo/_search
{
  "query": {
    "bool": {
      "filter": [
        {"term": {"catalog3Id": "61"}},
        {"term": {"skuAttrValueList.valueId":"160"}}
      ],
      "must": [
        {"match": {"skuName": "锤字手机"}}
      ]
    }
  },
  "highlight": {
    "pre_tags": ["<span style=color=red>"],
    "post_tags": ["</span>"],
    "fields": {"skuName": {}}
  },
  "from": 0,
  "size": 20,
  "sort": [
    {
      "hotScore": {
        "order": "desc"
      }
    }
  ],
  "aggs": {
    "groupby_attr": {
      "terms": {
        "field": "skuAttrValueList.valueId"
      }
    }
  }
}
```

**Es 匹配根据中文分词解析库进行匹配**

#### 2制作传入参数的类

在itmall-bean 项目中添加如下实体类 传入的参数是根据dsl语句得到

```java
package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author Ye Linfang
 * @date 2021/8/13 17:40
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuLsParams implements Serializable {
    String keyword;

    String catalog3Id;

    String[] valueId;

    int pageNo = 1;

    int pageSize = 20;
}
```

#### 3 返回结果的类

```java
package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/13 17:44
 * @description TODO
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class SkuLsResult implements Serializable {
    List<SkuLsInfo> skuLsInfoList;

    long total;

    long totalPages;

    List<String> attrValueIdList;
}
```

#### 4 基于这个DSL查询编写Java代码

```java
//接口 ListService
public SkuLsResult search(SkuLsParams skuLsParams);
//实现类
 @Override
    public SkuLsResult search(SkuLsParams skuLsParams) {
        //动态生成查询语句
        String query = makeQueryStringForSearch(skuLsParams);
        //定制查询动作
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        try {
            //执行查询动作，返回查询结果
            SearchResult searchResult = jestClient.execute(search);
            //处理，封装查询结果
            SkuLsResult skuLsResult = makeResultForSearch(skuLsParams, searchResult);
            log.debug("skuLsResult={}", skuLsResult);
            return skuLsResult;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
```

#### 5 构造查询DSL（动态生成dsl语句）

查询的过程很简单，但是要构造查询的query这个字符串有点麻烦，主要是这个Json串中的数据都是动态的。要拼接这个字符串，需要各种循环判断，处理标点符号等等。操作麻烦，可读性差。

 但是JestClient这个客户端包，提供了一组builder工具。这个工具可以比较方便的帮程序员组合复杂的查询Json。

```java
 //动态生成查询语句  
private String makeQueryStringForSearch(SkuLsParams skuLsParams) {
        // 创建SearchSourceBuilder查询源建造器
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		//创建BoolQueryBuilder查询建造器
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        //判断是否有查询条件keyword
        if (!StringUtils.isEmpty(skuLsParams.getKeyword())) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("skuName", skuLsParams.getKeyword());
            boolQueryBuilder.must(matchQueryBuilder);
            // 设置高亮
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    .field("skuName")
                    .preTags("<span style=color:red>")
                    .postTags("</span>");
            // 将高亮结果放入查询器中
            searchSourceBuilder.highlight(highlightBuilder);
        }

        // 设置三级分类
        if (!StringUtils.isEmpty(skuLsParams.getCatalog3Id())) {
            TermQueryBuilder termQueryBuilder = new TermQueryBuilder("catalog3Id", skuLsParams.getCatalog3Id());
            boolQueryBuilder.filter(termQueryBuilder);
        }
        // 设置属性值
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (int i = 0; i < skuLsParams.getValueId().length; i++) {
                String valueId = skuLsParams.getValueId()[i];
                boolQueryBuilder
                        .filter(new TermQueryBuilder("skuAttrValueList.valueId", valueId));
            }
        }
        searchSourceBuilder.query(boolQueryBuilder);
        // 设置分页
        int from = (skuLsParams.getPageNo() - 1) * skuLsParams.getPageSize();
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(skuLsParams.getPageSize());
        // 设置按照热度排序
        searchSourceBuilder.sort("hotScore", SortOrder.DESC);
        //设置聚合
        TermsBuilder groupby_attr = AggregationBuilders.terms("groupby_attr").field("skuAttrValueList.valueId");
        searchSourceBuilder.aggregation(groupby_attr);

        String query = searchSourceBuilder.toString();
        return query;
    }
```

#### 6 制作、处理返回值参数

思路：所有的返回值其实都在这个searchResult中

```java
searchResult = jestClient.execute(search);
```

它的结构其实可以在kibana 中观察一下：

**命中的结果**

![image-20210813212356506](images/image-20210813212356506.png)

**高亮显示**

<img src="images/image-20210813212430625.png" alt="image-20210813212430625" style="zoom: 80%;" />

**分组统计结果：**

<img src="images/image-20210813212458983.png" alt="image-20210813212458983" style="zoom:80%;" />

针对这三个部分来解析searchResult

```java
//处理，封装查询结果
private SkuLsResult makeResultForSearch(SkuLsParams skuLsParams, SearchResult searchResult) {
        //创建存储SkuLsInfo集合对象
        List<SkuLsInfo> skuLsInfoList = new ArrayList<>(skuLsParams.getPageSize());
        //获取hits
        List<SearchResult.Hit<SkuLsInfo, Void>> hits = searchResult.getHits(SkuLsInfo.class);
        for (SearchResult.Hit<SkuLsInfo, Void> hit : hits) {
            //获取SkuLsInfo对象
            SkuLsInfo skuLsInfo = hit.source;
            if (hit.highlight != null && hit.highlight.size() > 0) {
                List<String> skuNameList = hit.highlight.get("skuName");
                //把带有高亮标签的字符串替换skuName
                skuLsInfo.setSkuName(skuNameList.get(0));
            }
            skuLsInfoList.add(skuLsInfo);
        }

        //取记录个数并计算出总页数
        long totalPage = (searchResult.getTotal() + skuLsParams.getPageSize() - 1)
                / skuLsParams.getPageSize();

        //取出涉及的属性值id
        List<String> attrValueIdList = new ArrayList<>();
        MetricAggregation aggregations = searchResult.getAggregations();
        TermsAggregation groupby_attr = aggregations.getTermsAggregation("groupby_attr");
        if (groupby_attr != null) {
            List<TermsAggregation.Entry> buckets = groupby_attr.getBuckets();
            for (TermsAggregation.Entry bucket : buckets) {
                attrValueIdList.add(bucket.getKey());
            }
        }

        //创建返回结果对象，设置并各个属性值
        //设置skuLsInfoList、总条数、总页数、属性值id集合
        return new SkuLsResult()
                .setSkuLsInfoList(skuLsInfoList)
                .setTotal(searchResult.getTotal())
                .setTotalPages(totalPage)
                .setAttrValueIdList(attrValueIdList);
    }
```

测试后台程序…

#### 7 创建itmall-list-web模块

pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <artifactId>itmall-parent</artifactId>
        <groupId>com.wgml</groupId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../itmall-parent/pom.xml</relativePath>
    </parent>
    <groupId>com.wgml</groupId>
    <artifactId>itmall-list-web</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>itmall-list-web</name>
    <description>Demo project for Spring Boot</description>
    <properties>
        <java.version>1.8</java.version>
    </properties>
    <dependencies>
        <dependency>
            <groupId>com.wgml.itmall</groupId>
            <artifactId>itmall-interface</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.wgml.itmall</groupId>
            <artifactId>itmall-web-util</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>

```

application.properties配置文件

```properties
server.port=8506

spring.thymeleaf.cache=false
spring.thymeleaf.mode=LEGACYHTML5

spring.dubbo.application.name=list-web
spring.dubbo.registry.protocol=zookeeper
spring.dubbo.registry.address=192.168.72.139:2181
spring.dubbo.base-package=com.wgml.itmall
spring.dubbo.protocol.name=dubbo
spring.dubbo.consumer.timeout=10000
spring.dubbo.consumer.check=false

```

Controller层

```java
package com.wgml.itmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.wgml.itmall.bean.list.SkuLsParams;
import com.wgml.itmall.bean.list.SkuLsResult;
import com.wgml.itmall.service.list.ListService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author Ye Linfang
 * @date 2021/8/13 22:48
 * @description TODO
 * @since JDK1.8
 */
@Controller
@Slf4j
public class ListController {
    @Reference
    private ListService listService;

    @RequestMapping("/list.html")
    @ResponseBody
    public String getSkuLsInfoList(SkuLsParams skuLsParams){
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        return JSON.toJSONString(skuLsResult);
    }
}
```

#### 8 域名更改

1.修改nginx.conf 配置

​     \# vim /usr/local/nginx/conf/nginx.conf

2 具体修改

```shell
  upstream manage.itmall.com {
                server 192.168.72.1:8502;
        }
    server {
                listen       80;
                server_name  manage.itmall.com;
                location /{
                        proxy_pass http://manage.itmall.com;
                }
        }

    upstream list.itmall.com{
                server 192.168.72.1:8506;
        }
        server {
                listen       80;
                server_name  list.itmall.com;
                location /{
                        proxy_pass http://list.itmall.com;
                }
        }
```

3 重启nginx

```shell
./nginx -s quit #重启

./nginx quit #退出重启 

./nginx -s reload #重启
```

启动状态下 重新读取nginx.conf  reload

在hosts 配置文件中添加

\# hosts 配置文件

![image-20210813225554589](images/image-20210813225554589.png)

如果出现错误，先从query dsl 语句开始！

 测试：直接http://www.itmall.com/ 输入检索条件即可！

![image-20210813235547870](images/image-20210813235547870.png)

## 8.2列表功能展示实现、面包屑、数据展示

### 1 回顾

```java
电商第九天：
	全文检索：
		1.	商品上架  mysql --- es
			1.1	单个商品上传！
			1.2	批量上传？
			
			1.3	自动上传
				logstash：日志收集
					从数据库中将数据检索出来，直接添加到es 中！
					
				特别消耗性能：
					input:
					
					output:
					
					
					安装 linux 上！
					
					input {
						stdin {}
						jdbc {
							type => "jdbc"
							 # 数据库连接地址
							jdbc_connection_string => "jdbc:mysql://192.168.1.1:3306/TestDB?characterEncoding=UTF-8&autoReconnect=true""
							 # 数据库连接账号密码；
							jdbc_user => "username"
							jdbc_password => "pwd"
							 # MySQL依赖包路径；
							jdbc_driver_library => "mysql/mysql-connector-java-5.1.34.jar"
							 # the name of the driver class for mysql
							jdbc_driver_class => "com.mysql.jdbc.Driver"
							 # 数据库重连尝试次数
							connection_retry_attempts => "3"
							 # 判断数据库连接是否可用，默认false不开启
							jdbc_validate_connection => "true"
							 # 数据库连接可用校验超时时间，默认3600S
							jdbc_validation_timeout => "3600"
							 # 开启分页查询（默认false不开启）；
							jdbc_paging_enabled => "true"
							 # 单次分页查询条数（默认100000,若字段较多且更新频率较高，建议调低此值）；
							jdbc_page_size => "500"
							 # statement为查询数据sql，如果sql较复杂，建议配通过statement_filepath配置sql文件的存放路径；
							 # sql_last_value为内置的变量，存放上次查询结果中最后一条数据tracking_column的值，此处即为ModifyTime；
							 # skuInfo 表中读取数据到es  select * from skuInfo 
							 # statement_filepath => "mysql/jdbc.sql"
							statement => "SELECT KeyId,TradeTime,OrderUserName,ModifyTime FROM `DetailTab` WHERE ModifyTime>= :sql_last_value order by ModifyTime asc"
							 # 是否将字段名转换为小写，默认true（如果有数据序列化、反序列化需求，建议改为false）；
							lowercase_column_names => false
							 # Value can be any of: fatal,error,warn,info,debug，默认info；
							sql_log_level => warn
							 #
							 # 是否记录上次执行结果，true表示会将上次执行结果的tracking_column字段的值保存到last_run_metadata_path指定的文件中；
							record_last_run => true
							 # 需要记录查询结果某字段的值时，此字段为true，否则默认tracking_column为timestamp的值；
							use_column_value => true
							 # 需要记录的字段，用于增量同步，需是数据库字段
							tracking_column => "ModifyTime"
							 # Value can be any of: numeric,timestamp，Default value is "numeric"
							tracking_column_type => timestamp
							 # record_last_run上次数据存放位置；
							last_run_metadata_path => "mysql/last_id.txt"
							 # 是否清除last_run_metadata_path的记录，需要增量同步时此字段必须为false；
							clean_run => false
							 #
							 # 同步频率(分 时 天 月 年)，默认每分钟同步一次；
							schedule => "* * * * *"
						}
					}
					filter {
						json {
							source => "message"
							remove_field => ["message"]
						}
						# convert 字段类型转换，将字段TotalMoney数据类型改为float；
						mutate {
							convert => {
								"TotalMoney" => "float"
							}
						}
					}
					output {
						elasticsearch {
							 # host => "192.168.1.1"
							 # port => "9200"
							 # 配置ES集群地址
							hosts => ["192.168.1.1:9200", "192.168.1.2:9200", "192.168.1.3:9200"]
							 # 索引名字，必须小写
							index => "consumption"
							 # 数据唯一索引（建议使用数据库KeyID）
							document_id => "%{KeyId}"
						}
						stdout {
							codec => json_lines
						}
					}
					
				
		2.	通过用户输入的检索条件，将数据从es 中检索处理！
			面向对象：
				输入的参数：class
				
				输出的数据：class
			
			dsl 语句：
				根据dsl 语句用java 代码动态生成！
				
	
	将数据显示到页面上！
	1.	将skuLsInfo 循环显示到页面上！
		1.1	导入静态页面
			
			注：redis 自启动
				vim /etc/rc.d/rc.local
				/usr/local/redis/bin/redis-server /usr/local/redis/bin/redis.conf 
		1.2	测试：
			gmall-manage-service,gmall-list-service,gmall-list-web,gmall-item-web
			
			
	
	2.	平台属性，平台属性值
		京东：
			list
			
			search 
			
		正常业务：
			可以通过三级分类Id 查询平台属性，平台属性值。
			
			全文检索：可以通过聚合的平台属性值Id 查找平台属性，平台属性值。
			
		我们项目：完全可以通过聚合的平台属性值Id ，得到平台属性，平台属性值！
			
	
	3.	平台属性值过滤
		本质：将用户点击的平台属性值名称对应的Id 值，直接拼接到url 后面！
			3.1	用户点击平台属性值名称的时候，重新访问 list.html这个控制器！
					http://list.gmall.com/list.html?keyword=%E6%89%8B%E6%9C%BA&valueId=13
			3.2	运行内存在显示平台属性的位置消失了！
					3.2.1	原来的平台属性名称，平台属性值名称 存在当前集合中baseAttrInfoList
					3.2.2	用户点击之后，将平台属性值Id 放入url 上，实际是将参数给skuLsParams
							制作一个urlParam 来保存url的参数！
							
					3.2.3	baseAttrInfoList 中平台属性值对象的Id 如果有 skuLsParams.valueId 相同，则将baseAttrInfoList 集合中对应的数据移除即可！
							// 必须迭代器itco
						
	4.	面包屑
			4.1	先结构业务：
				概念：通过平台属性值筛选业务！
				组成：平台属性名称：平台属性值名称！
			
			4.2	将面包屑存储在集合中！
				在后台将面包屑保存起来！
				将面包屑的名称 ；保存到了BaseAttrValue 的valueName 中！
				在平台属性值对象中，声明一个变量！来记录最新的url 参数列表

			4.3	如何点击面包屑，再使查询条件变为原来的！
				原理：面包屑中藏着未拼接的urlParams
					4.3.1	在点击面包屑再重新请求控制器！
					4.3.2	将面包屑中平台属性值Id 获取到！是在baseAttrInfoList 集合中对应的数据移除地添加
					4.3.3	获取之后，与 skuLsParams 中的 valueId 做比较
						4.3.3.1	ture：则 urlParam 后面则不拼接当前的valueId
						4.3.3.2	fasle: 不管！
				
	5.	分页
			5.1	分页插件：
			
			5.2	注意数据设置的位置
				
	
	6.	热度排名
			根据用户查看商品的次数，来决定商品的热度，
			如果热度越高，则商品靠前！反之。
			
			查看商品详情的时候，查询次数+1！
				技术角度如何实现！
					skuLsInfo 
						hotScore = 记录热度排名
					
			用户访问一次商品，则需要更新一次es 么？
				有IO 操作！
			
				借助：redis 记录用户访问的次数！
				当用户访问次数达到10次的时候，则更新一下es！
			
			6.1	使用redis 哪种数据类型！
				 zset！
				 // 
				 jedis.zincrby(key,increment,member);
				 
				 接口：如何定义


```



### 代码如下：

### 2 list-controller层

```java
package com.wgml.itmall.list.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wgml.itmall.bean.list.Crumbs;
import com.wgml.itmall.bean.list.SkuLsParams;
import com.wgml.itmall.bean.list.SkuLsResult;
import com.wgml.itmall.bean.manage.BaseAttrInfo;
import com.wgml.itmall.bean.manage.BaseAttrValue;
import com.wgml.itmall.service.list.ListService;
import com.wgml.itmall.service.manage.ManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/13 22:48
 * @description TODO
 * @since JDK1.8
 */
@Controller
@Slf4j
public class ListController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    @RequestMapping("/empList")
    public String empList(){
        return "emptylist";
    }

    @RequestMapping("/list.html")
    public String getSkuLsInfoList(SkuLsParams skuLsParams, Model model) {
        // 设置每页显示的条数
        skuLsParams.setPageSize(2);
        //es中按条件搜索筛选，返回SkuLsResult结果
        SkuLsResult skuLsResult = listService.search(skuLsParams);
        if(skuLsResult == null || skuLsResult.getTotal() <= 0){
            return "redirect:/empList";
        }
        //获取平台属性值id集合
        List<String> attrValueIdList = skuLsResult.getAttrValueIdList();
        //根据平台属性值id集合获取平台属性、平台属性值名称等信息
        List<BaseAttrInfo> baseAttrInfoList = manageService.getAttrList(attrValueIdList);
        //制作一个urlParam 来保存url的参数！
        String urlParams = makeUrlParams(skuLsParams);
        //定义一个存储面包屑的集合
        List<Crumbs> crumbsList = new ArrayList<>();
        //baseAttrInfoList 中平台属性值对象的Id 如果有 skuLsParams.valueId 相同，则将baseAttrInfoList 集合中对应的数据移除
        for (Iterator<BaseAttrInfo> iterator = baseAttrInfoList.iterator(); iterator.hasNext(); ) {
            BaseAttrInfo baseAttrInfo = iterator.next();
            for (BaseAttrValue baseAttrValue : baseAttrInfo.getAttrValueList()) {
                if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
                    for (String valueId : skuLsParams.getValueId()) {
                        if (baseAttrValue != null && baseAttrValue.getId().equals(valueId)) {
                            //传参的属性值id和baseAttrInfoList中平台属性值对象的Id相等，则添移除这个baseAttrInfo
                            iterator.remove();
                            //制作面包屑
                            Crumbs crumbs = new Crumbs()
                                    .setName(baseAttrInfo.getAttrName() + "：" + baseAttrValue.getValueName())
                                    .setUrlParams(makeUrlParams(skuLsParams, valueId));
                            //添加面包屑到集合
                            crumbsList.add(crumbs);
                        }
                    }
                }
            }

        }

        //获取商品基本信息
        model.addAttribute("skuLsInfoList", skuLsResult.getSkuLsInfoList());
        //根据条件筛选出来的属性和属性值集合
        model.addAttribute("baseAttrInfoList", baseAttrInfoList);
        //请求路径url参数字符串
        model.addAttribute("urlParams", urlParams);
        log.debug("urlParams={}", urlParams);
        //keyword保存在作用域中,keyword可能为null
        model.addAttribute("keyword", skuLsParams.getKeyword());
        //面包屑
        model.addAttribute("crumbsList", crumbsList);
        //总页数
        model.addAttribute("totalPages", skuLsResult.getTotalPages());
        //页码
        model.addAttribute("pageNo", skuLsParams.getPageNo());
        return "list";
    }

    private String makeUrlParams(SkuLsParams skuLsParams, String... excludeValueIds) {
        String urlParams = "";
        if (!StringUtils.isEmpty(skuLsParams.getKeyword())) {
            urlParams = "keyword=" + skuLsParams.getKeyword();
        }
        if (!StringUtils.isEmpty(skuLsParams.getCatalog3Id())) {
            if (!StringUtils.isEmpty(urlParams)) {
                urlParams += "&";
            }
            urlParams += ("catalog3Id=" + skuLsParams.getCatalog3Id());
        }
        if (skuLsParams.getValueId() != null && skuLsParams.getValueId().length > 0) {
            for (String valueId : skuLsParams.getValueId()) {
                if (excludeValueIds != null
                        && excludeValueIds.length > 0 && excludeValueIds[0].equals(valueId)) {
                    // 跳出代码，后面的参数则不会继续追加【后续代码不会执行】
                    // 不能写break；如果写了break；其他条件则无法拼接！
                    continue;
                }
                //正常拼接
                if (!StringUtils.isEmpty(urlParams)) {
                    urlParams += "&";
                }
                urlParams += ("valueId=" + valueId);
            }
        }
        return urlParams;
    }
}

```

```java
//面包屑
package com.wgml.itmall.bean.list;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ye Linfang
 * @date 2021/8/15 1:20
 * @description 面包屑
 * @since JDK1.8
 */
@Data
@Accessors(chain = true)
public class Crumbs {
    /**
     * @description 面包屑名称 格式：（属性名称：属性值名称）
     */
    private String name;
    /**
     * @description 面包屑去除跳转url地址
     */
    private String urlParams;
}

```

```java
//html页面代码

<div class="GM_ipone">
    <div class="GM_ipone_bar">
        <div class="GM_ipone_one a">
            筛选条件
        </div>
        <i><img src="/image/right-@1x.png" alt=""></i>
        <span th:if="${keyword} != null" th:text="'&quot;'+${keyword}+'&quot;'"></span>
        <a class="select-attr" th:href="'/list.html?'+${crumbs.urlParams}" th:utext="${crumbs.name}+'<b> ✖ </b>'" th:each="crumbs:${crumbsList}">
            屏幕尺寸:5.1-5.5英寸<b> ✖ </b>
        </a>

    </div>
</div>

<!--商品筛选和排序-->
<div class="GM_banner w">
    <div class="GM_nav">
        <div class="GM_selector">
            <!--手机商品筛选-->
            <div class="title">
                <h3><em>商品筛选</em></h3>

            </div>
            <div class="GM_nav_logo">

                <div class="GM_pre" th:each="baseAttrInfo:${baseAttrInfoList}">
                    <div class="sl_key">
                        <span th:text="${baseAttrInfo.attrName}">属性1111：</span>
                    </div>
                    <div class="sl_value">
                        <ul>
                            <li th:each="attrValue:${baseAttrInfo.attrValueList}">
                                <a th:href="'/list.html?'+${urlParams}+'&valueId='+${attrValue.id}" th:text="${attrValue.valueName}">属性值111</a>
                            </li>
                        </ul>
                    </div>
                </div>

            </div>
        </div>
```





### 3 **列表页面和详情页面业务逻辑**

```java
list-service模块:es操作

manage-service模块：mysql操作

elasticsearch ：  存业务需要处理的数据

onSale()：商品上架，从redis中拿取数据，存业务需要处理的数据skuLsInfo到elasticsearch

redis： 缓存mysql中获取的数据

getSkuInfoByRedis()：先从redis拿数据，如果没有，则从mysql中拿取数据（getSkuInfoDB），然后把mysql数据缓存到redis，使用分布式锁，缓存崩溃去mysql拿，若后台有修改数据，则删除redis缓存数据。

列表页面：Controller --> list.html?skuLsParam=

​		从elastic中获取数据、基本信息展示、平台属性、属性值展示、点击平台属性值筛选：url地址拼接、平台属性、属性值去除、面包屑制作！没有商品则返回空页面

详情页面：Controller -->xxx.html？

​	1. checkOnSale()  检查商品是否上架：elasticsarch查找判断

​	 	true:根据skuId去redis缓存数据库拿取数据(getSkuInfoByRedis())，切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配

​		false:返回首页；v
```

### 4 详情页面代码优化修改：

bug：未上架的商品也能通过后台查询到数据展示

### 5 onSale()

```java
    /**
     * @param skuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 11:37
     * @description 商品上架
     */
    @RequestMapping("/onSale")
    @ResponseBody
    public String onSale(@RequestParam String skuId) {
        //检查是否已经上架
        Boolean checkOnSale = listService.checkOnSale(skuId);
        //上架 把数据库中的数据保存到es
        //后台拿数据，后台从redis拿skuInfo数据拷贝给skuLsInfo
        SkuLsInfo skuLsInfo = manageService.getSkuLsInfo(skuId);

        Boolean flat = listService.saveSkuLsInfo(skuLsInfo);
        if (flat == true) {
            //上架过了直接返回
            if (checkOnSale == true) {
                return "重新上架商品";
            }
            return "上架成功";
        }
        return "上架失败";
    }
//==========================================================
   @Override
    public SkuLsInfo getSkuLsInfo(String skuId) {
        SkuLsInfo skuLsInfo = new SkuLsInfo();
        SkuInfo skuInfo = getSkuInfoByRedis(skuId);
        BeanUtils.copyProperties(skuInfo, skuLsInfo);
        return skuLsInfo;
    }
//================================================================
    @Autowired
    private JestClient jestClient;
    public static final String ES_INDEX = "itaomall";
    public static final String ES_TYPE = "SkuInfo";

    @Override
    public Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        // 保存数据
        Index index = new Index.Builder(skuLsInfo).index(ES_INDEX).type(ES_TYPE).id(skuLsInfo.getId()).build();
        try {
            DocumentResult documentResult = jestClient.execute(index);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
```



### 6 getSkuInfoByRedis()

```java
    @Override
    public SkuInfo getSkuInfoByRedis(String skuId) {
//        return getInfoJedis(skuId);
        return getInfoRedission(skuId);
    }
```



### 7 优化后的getSkuInfoDB（)

```java
  /**
     * @param skuId
     * @return: com.wgml.itmall.bean.manage.SkuInfo
     * @author Ye Linfang
     * @date 2021/8/15 12:56
     * @description 从mysql获取数据，封装skuInfo,缓存进redis,
     */
    @Override
    public SkuInfo getSkuInfoDB(String skuId) {
        //查询基本信息
        SkuInfo skuInfo = skuInfoMapper.selectByPrimaryKey(skuId);
        //查询图片集合
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(skuId);
        List<SkuImage> skuImageList = skuImageMapper.select(skuImage);
        //查询平台属性值集合 用于存到es
        SkuAttrValue skuAttrValue = new SkuAttrValue();
        skuAttrValue.setSkuId(skuId);
        List<SkuAttrValue> skuAttrValueList = skuAttrValueMapper.select(skuAttrValue);
        //设置valuesSkuIdJson 用于存到es   格式：valueid1|valueid2|valueid3|... ：skuId
        String valuesSkuJson = makeValuesSkuIdString(skuId);
        //设置各个属性
        skuInfo.setSkuImageList(skuImageList)
                //查询销售属性,设置属性，用于详情页回显锁定
                .setSpuSaleAttrList(getSpuSaleAttrListCheckBySku(skuInfo))
                .setSkuAttrValueList(skuAttrValueList)
                .setValuesSkuJson(valuesSkuJson);
        log.debug("数据库DB查询返回SkuInfo数据");
        return skuInfo;
    }
//=========================================================================
 /**
     * @param skuId
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 18:45
     * @description //设置valuesSkuIdJson 用于valueid1|valueid2|valueid3|... ：skuId
     */
    private String makeValuesSkuIdString(String skuId) {
        List<SkuSaleAttrValue> skuSaleAttrValueList = skuSaleAttrValueMapper.select(new SkuSaleAttrValue().setSkuId(skuId));
        String valueIds = "";
        for (SkuSaleAttrValue skuSaleAttrValue : skuSaleAttrValueList) {
            if (!StringUtils.isEmpty(valueIds)) {
                valueIds += "|";
            }
            valueIds += skuSaleAttrValue.getSaleAttrValueId();
        }
        valueIds += ":" + skuId;
        return valueIds;
    }
```



### 8 checkOnSale()

```java
 /**
      * @param skuId
      * @return: java.lang.Boolean
      * @author Ye Linfang
      * @date 2021/8/13 18:33
      * @description 检查商品是否上架 true 上架  false 没上架
      */ 
@Override
    public Boolean checkOnSale(String skuId) {
        //创建查询条件
        String query = new SearchSourceBuilder()
                .query(QueryBuilders.matchQuery("_id", skuId))
                .toString();
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        SearchResult result = null;
        try {
            result = jestClient.execute(search);
            if (result.getTotal() == 1) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
```



### 9 makeValuesSkuJsonByES()

```java
  /**
      * @param spuId
      * @return: java.lang.String
      * @author Ye Linfang
      * @date 2021/8/15 20:02
      * @description 切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配
      */
@Override
    public String makeValuesSkuJsonByES(String spuId) {
        String query = "{\n" +
                "  \"query\": {\n" +
                "  \"bool\": {\n" +
                "    \"filter\": [{\"term\": {\"spuId\": \"" + spuId + "\"}}]\n" +
                "    }\n" +
                "  }\n" +
                "  ,\n" +
                "  \"aggs\": {\n" +
                "  \"groupby_valuesSku\": {\n" +
                "    \"terms\": {\n" +
                "    \"field\": \"valuesSkuJson\"\n" +
                "    }\n" +
                "  }\n" +
                "  }\n" +
                "}";
        Search search = new Search.Builder(query).addIndex(ES_INDEX).addType(ES_TYPE).build();
        Map<String, String> map = new HashMap<>();
        try {
            SearchResult searchResult = jestClient.execute(search);
            MetricAggregation aggregations = searchResult.getAggregations();
            TermsAggregation groupby_valuesSku = aggregations.getTermsAggregation("groupby_valuesSku");
            if(groupby_valuesSku != null){
                List<TermsAggregation.Entry> buckets = groupby_valuesSku.getBuckets();
                for (TermsAggregation.Entry bucket : buckets) {
                    //117|119:33
                    String key = bucket.getKey();
                    String[] split = key.split(":");
                    map.put(split[0],split[1]);
                }
            }
            return JSON.toJSONString(map);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
```



### 10 详情页面controller层代码

```java
package com.wgml.itmall.item.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wgml.itmall.bean.manage.SkuInfo;
import com.wgml.itmall.bean.manage.SpuSaleAttr;
import com.wgml.itmall.service.list.ListService;
import com.wgml.itmall.service.manage.ManageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/8/9 11:54
 * @description TODO
 * @since JDK1.8
 */
@Controller
@Slf4j
public class ItemController {
    @Reference
    private ListService listService;
    @Reference
    private ManageService manageService;

    /**
     * @param skuId:
     * @param model:
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/15 12:51
     * @description 商品详情页控制器 从es拿数据
     */
    @RequestMapping("/{skuId}.html")
    public String skuInfoPage(@PathVariable("skuId") String skuId, Model model) {
        //检查商品是否上架，elasticsarch查找判断
        Boolean flat = listService.checkOnSale(skuId);
        if (!flat) {
            //没有上架，直接返回首页
            return "redirect:http://www.itaomall.com/";
        }
        // 上架了商品，根据skuId去redis缓存数据库拿取skuInfo数据
        SkuInfo skuInfo = manageService.getSkuInfoByRedis(skuId);
        // 存储 spu，sku数据 回显锁定
        List<SpuSaleAttr> saleAttrList = skuInfo.getSpuSaleAttrList();
        //切换属性值操作：从elasticsearch拿valuesSkuJson进行匹配
        String valuesSkuJson = listService.makeValuesSkuJsonByES(skuInfo.getSpuId());
        if(StringUtils.isEmpty(valuesSkuJson)){
            return "redirect:http://www.itaomall.com/";
        }
        model.addAttribute("saleAttrList", saleAttrList);
        model.addAttribute("skuInfo", skuInfo);
        //把map变成json串并放入请求域中
        model.addAttribute("valuesSkuJson", valuesSkuJson);
        log.debug("valuesSkuJson====>{}", valuesSkuJson);
        return "item";
    }

//之前版本  淘汰原因：需要上架后商品
//    @RequestMapping("/{skuId}.html")
//    public String skuInfoPage(@PathVariable("skuId") String skuId, Model model) {
//        // 存储基本的skuInfo信息
//        SkuInfo skuInfo = manageService.getSkuInfo(skuId);
//        model.addAttribute("skuInfo", skuInfo);
//        // 存储 spu，sku数据 回显锁定
//        List<SpuSaleAttr> saleAttrList = skuInfo.getSpuSaleAttrList();
//        model.addAttribute("saleAttrList", saleAttrList);
//
//        //把列表变换成 valueid1|valueid2|valueid3 ：skuId  的 哈希表 用于在页面中定位查询
//        String valuesIdKey = "";
//        HashMap<String, Object> valuesSkuMap = new HashMap<>();
//        List<SkuSaleAttrValue> skuSaleAttrValueList = getSkuSaleAttrValueListBySpu(skuInfo.getSpuId);
//        if(skuSaleAttrValueList != null){
//            for (int i = 0; i < skuSaleAttrValueList.size(); i++) {
//                SkuSaleAttrValue skuSaleAttrValue = skuSaleAttrValueList.get(i);
//                if (valuesIdKey.length() != 0) {
//                    valuesIdKey += "|";
//                }
//                valuesIdKey += skuSaleAttrValue.getSaleAttrValueId();
//                if ((i + 1) == skuSaleAttrValueList.size() || !skuSaleAttrValue.getSkuId().equals(skuSaleAttrValueList.get(i + 1).getSkuId())) {
//                    valuesSkuMap.put(valuesIdKey, skuSaleAttrValue.getSkuId());
//                    valuesIdKey = "";
//                }
//            }
//        }
//        //把map变成json串并放入请求域中
//        model.addAttribute("valuesSkuJson", JSON.toJSONString(valuesSkuMap));
//        log.debug("valuesSkuJson====>{}",JSON.toJSONString(valuesSkuMap));
//
//        return "item";
//    }

}

```

### 11 热度排名

	热度排名
			根据用户查看商品的次数，来决定商品的热度，
			如果热度越高，则商品靠前！反之。
			
			查看商品详情的时候，查询次数+1！
				技术角度如何实现！
					skuLsInfo 
						hotScore = 记录热度排名
					
			用户访问一次商品，则需要更新一次es 么？
				有IO 操作！
			
				借助：redis 记录用户访问的次数！
				当用户访问次数达到10次的时候，则更新一下es！
			
			6.1	使用redis 哪种数据类型！
				 zset！
				 // 
				 jedis.zincrby(key,increment,member);
				 
				 接口：如何定义

```java
1排序
页面结构完成了，考虑一下如何排序，es查询的dsl语句中我们是用了hotScore来进行排序的。
但是hotScore从何而来，根据业务去定义，也可以扩展更多类型的评分，让用户去选择如何排序。
这里的hotScore我们假定以点击量来决定热度。
那么我们每次用户点击，将这个评分+1,不就可以了么。
2问题：
    1、 es大量的写操作会影响es 性能，因为es需要更新索引，而且es不是内存数据库，会做相应的io操作。
    2、而且修改某一个值，在高并发情况下会有冲突，造成更新丢失，需要加锁，而es的乐观锁会恶化性能问题。
    从业务角度出发，其实我们为商品进行排序所需要的热度评分，并不需要非常精确，大致能比出个高下就可以了。
    利用这个特点我们可以稀释掉大量写操作。

3解决思路：
用redis做精确计数器，redis是内存数据库读写性能都非常快，利用redis的原子性的自增可以解决并发写操作。
 redis每计10次数（可以被10整除）我们就更新一次es ，这样写操作就被稀释了1倍，这个倍数可以根据业务情况灵活设定。

代码 在listServiceImpl中增加更新操作

```

itmall-list-service更新redis计数器

在配置文件中添加配置

```java
spring.redis.host=192.168.72.139
spring.redis.port=6379
spring.redis.database=0
```

注意：在启动类上添加扫描注解：@ComponentScan(basePackages = **"com.wgml.itmall"**)

```java
接口：
public void incrHotScore(String skuId);
在ListServiceImpl实现类中添加方法

//更新热度评分
@Override
public void incrHotScore(String skuId){
    Jedis jedis = redisUtil.getJedis();
    int timesToEs=10;
    Double hotScore = jedis.zincrby("hotScore", 1, "skuId:" + skuId);
    if(hotScore%timesToEs==0){
        updateHotScore(skuId,  Math.round(hotScore));
    }

}

```

更新es

```java
private void updateHotScore(String skuId,Long hotScore){
     String updateJson="{\n" +
             "   \"doc\":{\n" +
             "     \"hotScore\":"+hotScore+"\n" +
             "   }\n" +
             "}";

     Update update = new Update.Builder(updateJson).index(ES_INDEX).type(ES_TYPE).id(skuId).build();
    try {
        jestClient.execute(update);
    } catch (IOException e) {
        e.printStackTrace();
    }
}

```

这个incrHotScore 方法可以在进入详情页面的时候调用

```java
@Controller
public class ItemController {

    @Reference
    ListService listService;

    @Reference
    ManageService manageService;

    @RequestMapping("/{skuId}.html")
    public String getSkuInfo(@PathVariable("skuId") String skuId, Model model){
     ……
listService.incrHotScore(skuId);  //最终应该由异步方式调用
}
```

### 12、es分页

```java
// 设置每页显示的条数
skuLsParams.setPageSize(2);

model.addAttribute("totalPages", skuLsResult.getTotalPages());
model.addAttribute("pageNo",skuLsParams.getPageNo());

//TODO 看编辑器里代码list.html js代码
```







## 9 es 扩知识

```java
es：支持集群：
	cluster-name : 相同  my-es
	
	多个节点：
	node-name = node-1 ,node-2
	
	port= 9200,9300
	
	节点通过下面属性组成集群
	discovery.zen.ping.unicast.hosts: ["192.168.67.218"]
		
	192.168.67.218:主节点
	data：节点


所谓脑裂问题（类似于精神分裂），就是同一个集群中的不同节点，对于集群的状态有了不一样的理解。
1.	网络：由于是内网通信，网络通信问题造成某些节点认为master死掉，而另选master。

2. 节点负载：由于master节点与data节点都是混合在一起的，所以当工作节点的负载较大（确实也较大）时，导致对应的ES实例停止响应，
而这台服务器如果正充当着master节点的身份，那么一部分节点就会认为这个master节点失效了，故重新选举新的节点，这时就出现了脑裂；
同时由于data节点上ES进程占用的内存较大，较大规模的内存回收操作也能造成ES进程失去响应。所以，这个原因的可能性应该是最大的。

1. 对应于上面的分析，推测出原因应该是由于节点负载导致了master进程停止响应，继而导致了部分节点对于master的选择出现了分歧。
为此，一个直观的解决方案便是将master节点与data节点分离。为此，我们添加了三台服务器进入ES集群，不过它们的角色只是master节点，
不担任存储和搜索的角色，故它们是相对轻量级的进程。可以通过以下配置来限制其角色：
node.192.168.67.218: true
node.data: false

2. 还有两个直观的参数可以减缓脑裂问题的出现：

discovery.zen.ping_timeout（默认值是3秒）：默认情况下，一个节点会认为，如果master节点在3秒之内没有应答，那么这个节点就是死掉了，
而增加这个值，会增加节点等待响应的时间，从一定程度上会减少误判。

discovery.zen.minimum_master_nodes（默认是1）：这个参数控制的是，一个节点需要看到的具有master节点资格的最小数量，然后才能在集群中做操作。
官方的推荐值是(N/2)+1，其中N是具有master资格的节点的数量（我们的情况是3，因此这个参数设置为2，但对于只有2个节点的情况，设置为2就有些问题了，一个节点DOWN掉后，你肯定连不上2台服务器了，这点需要注意）。
-----------------------------------------------------------------
CURL的操作 
   curl是利用URL语法在命令行方式下工作的开源文件传输工具，使用curl可以简单实现常见的get/post请求。简单的认为是可以在命令行下面访问url的一个工具。在centos的默认库里面是有curl工具的，如果没有请yum安装即可。 
    curl 
    -X 指定http的请求方法 有HEAD GET POST PUT DELETE 
    -d 指定要传输的数据 
    -H 指定http请求头信息 
    浏览ES服务器 
        curl -XGET http://192.168.67.218:9200  <=> 在浏览器中访问 
    创建索引库 
        curl -XPUT http://192.168.67.218:9200/gmall 
        这样就在es中创建了一个索引库bigdata_p 
新增数据：
	curl -XPUT http://192.168.67.218:9200/gmall/SkuInfo/ -d '{"name"："hadoop", "author": "Doug Cutting", "c_version": "2.7.3"}' 
查询某一个索引库中的数据 
curl -XGET http://192.168.67.218:9200/gmall/_search?pretty
	 在url后面加上一个pretty则会对返回结果进行格式化。
查询某一个type：curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/_search?pretty 
查询具体的一条记录：curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/1?pretty 
查询一条索引文档中的具体的字段：curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/1?_source=name&pretty 
如果要查询多个字段，使用","进行隔开curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/1?_source=name,author&pretty 
获取source所有数据 
            curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/1?_source&pretty 
根据条件进行查询 
            curl -XGET http://192.168.67.218:9200/gmall/SkuInfo/_search?q=name:hbase,hive&pretty 
	
```









# 16、BaseResponse

```java
package com.wgml.itmall.bean.common;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/13 12:23
 * @description BaseResponse 统一返回结果数据封装类
 * @since JDK1.8
 */
@Data
//@Accessors(prefix = "is")
@Builder(toBuilder = true)
public class BaseResponse {
    private Integer code;
    private String message;
    private Boolean isSuccess;
    @Singular("dat")
    private Map<String, Object> data;

    public static BaseResponseBuilder okBuilder() {
        return BaseResponse.builder()
                .code(20000)
                .isSuccess(true)
                .message("成功");
    }

    public static BaseResponseBuilder errBuilder() {
        return BaseResponse.builder()
                .code(20001)
                .isSuccess(false)
                .message("失败");
    }

    public static BaseResponse error() {
        return BaseResponse.builder()
                .code(20001)
                .isSuccess(false)
                .message("失败")
                .build();
    }

    public static BaseResponse ok() {
        return BaseResponse.builder()
                .code(20000)
                .isSuccess(true)
                .message("成功")
                .build();
    }
}

```



# 17 单点登录

day10 单点登录

### **个人总结知识点：**

1、需要登录验证的页面控制器上添加@**LoginReque**(autoRedirect=true)注解

2、打开每个web页面之前都被拦截器**AuthInterceptor**拦截校验（注意包扫描和nginx服务ip地址获取配置和注解添加）

 																					proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
 	
 		1. AuthInterceptor拦截器功能：
 	     		1. 如果url上有newToken，则获取newToken值，把newToken放入Cookie中，调用方法为**CookieUtil**.setCookie（）；
 	     		2. 如果url没有newToken，则去Cookie中获取token值，方法为token = **CookieUtil**.getCookieValue（）；
 	     		3. 如果从Cookie中获取的token不为空，则调用**getUserMapByToken**（）方法获取nickName，并放到作用域中
 	     		4. 调用(**HandlerMethod**) **handler**.**getMethodAnnotation**()方法获取注解
 	            		1. 如果不为空，则通过**HttpClientUtil**.doGet(）方法传参ip地址和token参数调用认证中心的**验证方法**：1、利用密钥和IP检验token是否正确，并获得里面的userId。2、**用userId检查Redis中是否有用户信息**,如果有延长它的过期时间。3、登录成功状态返回success。
 	                 		1. 如果验证成功，则调用**getUserMapByToken**（）方法获取userId，并放到作用域中,并放行
 	                   		2. 如果验证失败
 	                          		1. 如果**loginRequie**.**autoRedirect**()=true，获取当前url地址并编码，带着当前url地址参数**重定向到登录页面**，并阻止放行
 	            		2. 为空则放行

2. 如果此页面需要登录验证（@**LoginReque**(autoRedirect=true)）
   1. 页面有token（url或cookie中）、token正确且redis中userKey没有失效，则验证能通过，验证通过后获取userid，nickname和重新延长userkey时效，放行
   2. 页面没有token，或者token错误，或者redis中userKey失效则验证不通过，带参重定向到登录页面，阻止放行
      1. 认证中心登录页面控制器获取originUrl地址传给页面，页面渲染
      2. 用户输入登录账号和密码，发起ajax请求去后台mysql数据库登录验证，登录成功后把用户信息放进redis缓存用于下次验证用户是否登录，登录成功后返回token给页面，页面接受验证成功token后，带参数newToken=token根据originUrl进行跳转连接，回到之前需要验证的地方

3. 如果此页面不需要登录验证（方法上没有注解或@**LoginReque**(autoRedirect=false)）
   1. 页面有token（url或cookie中）、token正确且redis中userKey没有失效，验证能通过则能获取userid，nickname和重新延长userkey时效，放行
   2. 页面没有token，或者token错误，或者redis中userKey失效则验证不通过，放行





### 1 单点登录业务介绍

早期单一服务器，用户认证。

![image-20210818143954862](images/image-20210818143954862.png)

缺点：单点性能压力，无法扩展

 分布式，SSO(single sign on)模式

![image-20210818144019326](images/image-20210818144019326.png)

解决 ： 

用户身份信息独立管理，更好的分布式管理。

​    可以自己扩展安全策略

   跨域不是问题

​       跨域：ip，port 不相同！ Jsonp={得到其他域的信息}！@CrossOrigin, httpClient

 缺点：

   认证服务器访问压力较大。

业务流程图{用户访问业务时，必须登录的流程}{单点登录的过程

![image-20210818144139237](images/image-20210818144139237.png)

### 2 认证中心模块

搭建模块

页面中的js

```html
<div class="si_bom1 tab" style="display: block;">
   <div  class="error">
      请输入账户名和密码
   </div>

   <form id="loginForm" action="/login" method="post">
   <ul>
      <li class="top_1">
         <img src="/img/user_03.png" class="err_img1"/>

         <input type="text" name="loginName" placeholder=" 邮箱/用户名/已验证手机"  class="user"/>
      </li>
      <li>
         <img src="/img/user_06.png" class="err_img2"/>
         <input type="password" name="passwd" placeholder=" 密码" class="password"/>

      </li>
      <li class="bri">
         <a href="">忘记密码</a>
      </li>
      <li class="ent"><button id="btn2" class="btn2"><a class="a">登 &nbsp; &nbsp;录</a></button></li>

   </ul>
      <input type="hidden" id="originUrl" name="originUrl" th:value="${originUrl}"/>
   </form>
</div>

btn2.onclick=function(){
   if(user.value == '' || pass.value ==''){
      err.style.display='block';
      user.style.border='1px solid red';
      pass.style.border='1px solid red';
       
      err_img1.src='img/img11.png';
      err_img2.src='img/img22.png';
   }else{
      $.post("/login",$("#loginForm").serialize(),function (data) {
         if(data&&data!='fail'){
             var originUrl = $("#originUrl").val();
            originUrl = decodeURIComponent(originUrl);

                         var idx=originUrl.indexOf('?');

                         if(idx<0){
                             originUrl+='?'
            }else{
                             originUrl+='&'
            }
             window.location.href=originUrl+"newToken="+data;
         }else{
                         $(".error").text("用户名密码错误！");
            $(".error").show();
         }
                 }  );


   }user.onfocus=function(){
   err_img1.src='img/grow1.png';
   user.style.border='1px solid #999';
}

}
```



```java
登录功能：
1、	用接收的用户名密码核对后台数据库
2、	将用户信息加载到写入redis，redis中有该用户视为登录状态。
3、	用userId+当前用户登录ip地址+密钥生成token
4、	重定向用户到之前的来源地址，同时把token作为参数附上。
验证功能：
1、	利用密钥和IP检验token是否正确，并获得里面的userId
2、	用userId检查Redis中是否有用户信息,如果有延长它的过期时间。
3、	登录成功状态返回。    
```

```java
package com.wgml.itmall.passport.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.wgml.itmall.bean.user.UserInfo;
import com.wgml.itmall.service.user.UserService;
import com.wgml.itmall.util.IpAddressUtil;
import com.wgml.itmall.util.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/16 14:23
 * @description TODO
 * @since JDK1.8
 */
@Controller
@Slf4j
public class PassportController {

    @Value("${token.key}")
    private String key;

    @Reference
    private UserService userService;

    /**
     * @param model
     * @param originUrl
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/16 15:23
     * @description 跳转到登录页面，携带原始地址参数用于登录成功后回调
     */
    @RequestMapping("/login.html")
    public String index(Model model, String originUrl) {

        model.addAttribute("originUrl", originUrl);
        return "index";
    }

    /**
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/16 15:23
     * @description 做登录处理  成功：返回Token 失败：返回fail
     */
    @RequestMapping("/login")
    @ResponseBody
    public String login(UserInfo userInfo, HttpServletRequest request) {
        if (userInfo == null || userInfo.getLoginName() == null || userInfo.getPasswd() == null) {
            return "fail";
        }
        UserInfo uInfo = userService.login(userInfo);
        if (uInfo == null) {
            return "fail";
        }
        HashMap<String, Object> map = new HashMap<>();
        String ipAddress_Salt = IpAddressUtil.getIpAddress(request);
        map.put("nickName", uInfo.getNickName());
        map.put("userId", uInfo.getId());
        String token = JwtUtil.encode(key, map, ipAddress_Salt);
        log.debug("iP={},token={}", ipAddress_Salt, token);
        return token;
    }

    /**
     * @param token
     * @param currentIp
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/17 16:52
     * @description 测试：http://www.passport.itmall.com/verify?token=eyJhbGciOiJIUzI1NiJ9
     * .eyJuaWNrTmFtZSI6IkFkbWluaXN0cmF0b3IiLCJ1c2VySWQiOiIyIn0
     * .93ZLchxF_JxG_hQ0wFW-n7IlY1J2f3fMZ_k3VqNw2HQ&currentIp=192.168.72.1
     */
    @RequestMapping("/verify")
    @ResponseBody
    public String verify(String token, String currentIp) {
        if (StringUtils.isEmpty(token) || StringUtils.isEmpty(currentIp)) {
            return "fail";
        }
        Map<String, Object> userMap = JwtUtil.decode(token, key, currentIp);
        if (userMap != null) {
            //检查redis信息
            String userId = (String) userMap.get("userId");
            UserInfo userInfo = userService.verify(userId);
            if (userInfo != null) {
                return "success";
            }
        }
        return "fail";
    }


}

```

```java
package com.wgml.itmall.user.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.wgml.itmall.bean.user.UserAddress;
import com.wgml.itmall.bean.user.UserInfo;
import com.wgml.itmall.config.RedisUtil;
import com.wgml.itmall.service.user.UserService;
import com.wgml.itmall.user.mapper.UserAddressMapper;
import com.wgml.itmall.user.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import redis.clients.jedis.Jedis;

import java.util.List;

/**
 * @author Ye Linfang
 * @date 2021/7/30 20:21
 * @description 用户实现类
 * @since JDK1.8
 */
@Slf4j
@Service //dubbo提供端
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;
    @Autowired
    UserAddressMapper userAddressMapper;
    @Autowired
    private RedisUtil redisUtil;
    public String userKey_prefix = "user:";
    public String userinfoKey_suffix = ":info";
    public int userKey_timeOut = 60 * 60 * 24;


    @Override
    public List<UserInfo> getUserInfoListAll() {
        return userMapper.selectAll();
    }

    @Override
    public List<UserAddress> getUserAddressListByUId(String userId) {
        UserAddress userAddress = new UserAddress();
        userAddress.setUserId(userId);
        return userAddressMapper.select(userAddress);
    }

    @Override
    public UserInfo login(UserInfo userInfo) {
        //md5加密处理
        String newPassword = DigestUtils.md5DigestAsHex(userInfo.getPasswd().getBytes());
        UserInfo uInfo = userMapper.selectOne(userInfo.setPasswd(newPassword));
        if (uInfo != null) {
            // 获得到redis ,将用户存储到redis中
            Jedis jedis = null;
            try {
                String userKey = userKey_prefix + uInfo.getId() + userinfoKey_suffix;
                jedis = redisUtil.getJedis();
                jedis.setex(userKey, userKey_timeOut, JSON.toJSONString(uInfo));
                return uInfo;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("缓存服务出错");
            } finally {
                jedis.close();
            }
        }
        return null;
    }

   @Override
    public UserInfo verify(String userId) {
        if (!StringUtils.isEmpty(userId)) {
            Jedis jedis = null;
            try {
                jedis = redisUtil.getJedis();
                String key = userKey_prefix + userId + userinfoKey_suffix;
                String userInfoJson = jedis.get(key);
                if (!StringUtils.isEmpty(userInfoJson)) {
                    // 延长时效
                    jedis.expire(key, userKey_timeOut);
                    return JSON.parseObject(userInfoJson, UserInfo.class);
                }
                jedis.close();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (jedis != null) {
                    jedis.close();
                }
            }
        }
        return null;
    }


}

```

### 3 业务模块页面登录情况检查

```java
1.	将登录之后的token 放入cookie 中
2.	使用拦截器来获取token！{token 中已经有用户的昵称}
3.	系统中哪些模块需要用户登录 {在需要登录的控制器上添加一个注解} 自定义注解
```

web-util模块下

AuthInterceptor

```java
package com.wgml.itmall.interceptor;

import com.alibaba.fastjson.JSON;
import com.wgml.itmall.util.*;
import io.jsonwebtoken.impl.Base64UrlCodec;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/17 17:01
 * @description TODO
 * @since JDK1.8
 */
@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getParameter("newToken");
        //把token保存到cookie
        if (!StringUtils.isEmpty(token)) {
            CookieUtil.setCookie(request, response, "token", token, WebConst.COOKIE_MAXAGE, false);
        }
        //获取Cookie中的token
        else {
            token = CookieUtil.getCookieValue(request, "token", false);
        }
        if (!StringUtils.isEmpty(token)) {
            //读取token
            Map<String, Object> map = getUserMapByToken(token);
            if (map != null) {
                String nickName = (String) map.get("nickName");
                request.setAttribute("nickName", nickName);
            }
        }
        HandlerMethod handle = (HandlerMethod) handler;
        LoginRequie loginRequie = handle.getMethodAnnotation(LoginRequie.class);
        if (loginRequie != null) {
            String currentIp = IpAddressUtil.getIpAddress(request);
            String result = HttpClientUtil.doGet(WebConst.VERIFY_ADDRESS + "?token=" + token + "&currentIp=" + currentIp);
            if ("success".equals(result)) {
                Map map = getUserMapByToken(token);
                String userId = (String) map.get("userId");
                request.setAttribute("userId", userId);
                return true;
            } else {
                if (loginRequie.autoRedirect()) {
                    String requestURL = request.getRequestURL().toString();
                    String encodeURL = UrlEncodeUtil.getURLEncoderString(requestURL);
                    response.sendRedirect(WebConst.LOGIN_ADDRESS + "?originUrl=" + encodeURL);
                    return false;
                }
            }
        }
        return true;
    }

    private Map<String, Object> getUserMapByToken(String token) {
        try {
            String tokenUserInfo = org.apache.commons.lang3.StringUtils.substringBetween(token, ".");
            Base64UrlCodec base64UrlCodec = new Base64UrlCodec();
            byte[] tokenBytes = base64UrlCodec.decode(tokenUserInfo);
            String tokenJson = new String(tokenBytes, "UTF-8");
            Map map = JSON.parseObject(tokenJson, Map.class);
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}


```

```java
package com.wgml.itmall.interceptor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Ye Linfang
 * @date 2021/8/17 18:45
 * @description TODO
 * @since JDK1.8
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginRequie {
    boolean autoRedirect() default true;
}

```

```java
package com.wgml.itmall.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Ye Linfang
 * @date 2021/8/17 18:42
 * @description 配置完拦截器之后，需要在web项目中添加包扫描
 * @since JDK1.8
 */
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}

```





### 4 工具类

#### JwtUtil工具类

```java
package com.wgml.itmall.util;

import io.jsonwebtoken.*;

import java.util.Map;

/**
 * @author Ye Linfang
 * @date 2021/8/16 19:31
 * @description json web token:一种基于JSON的开放标准，制作token
 */
public class JwtUtil {
    /**
     * @param key   公共部分：自定义一个KEY=字符串
     * @param param 私有部分：用户信息 {userId,nickName}
     * @param salt  签名部分：ip地址{服务器的} 192.168.67.1  ip地址+KEY=字符串 = 组成一个生成 jwt 的key ！
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/16 19:43
     * @description base64 编码： 将明文转化为暗文！
     */
    public static String encode(String key, Map<String, Object> param, String salt) {
        if (salt != null) {
            key += salt;
        }
        JwtBuilder jwtBuilder = Jwts.builder()
                .signWith(SignatureAlgorithm.HS256, key)
                .setClaims(param);

        String token = jwtBuilder.compact();
        return token;

    }

    /**
     * @param token
     * @param key
     * @param salt
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @author Ye Linfang
     * @date 2021/8/16 19:44
     * @description 根据key和盐，解析token 返回用户信息私有部分
     */
    public static Map<String, Object> decode(String token, String key, String salt) {
        Claims claims = null;
        if (salt != null) {
            key += salt;
        }
        try {
            claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
        } catch (JwtException e) {
            return null;
        }
        return claims;
    }

}
```

#### HttpClientUtil工具类

```java
package com.wgml.itmall.util;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @param
 * @return
 */


public class HttpClientUtil {

    public static String doGet(String url) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = EntityUtils.toString(entity, "UTF-8");
                EntityUtils.consume(entity);
                httpclient.close();
                return result;
            }
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return null;
    }


    public static void download(String url, String fileName) {

        // 创建Httpclient对象
        CloseableHttpClient httpclient = HttpClients.createDefault();
        // 创建http GET请求
        HttpGet httpGet = new HttpGet(url);
        CloseableHttpResponse response = null;
        try {
            // 执行请求
            response = httpclient.execute(httpGet);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();

                // String result = EntityUtils.toString(entity, "UTF-8");
                byte[] bytes = EntityUtils.toByteArray(entity);
                File file = new File(fileName);
                //  InputStream in = entity.getContent();
                FileOutputStream fout = new FileOutputStream(file);
                fout.write(bytes);

                EntityUtils.consume(entity);

                httpclient.close();
                fout.flush();
                fout.close();
                return;
            }
            httpclient.close();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        return;
    }
}

```



#### CookieUtil工具类

```java
package com.wgml.itmall.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @param
 * @return
 */
public class CookieUtil {


    public static String getCookieValue(HttpServletRequest request, String cookieName, boolean isDecoder) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookieName == null) {
            return null;
        }
        String retValue = null;
        try {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    if (isDecoder) {
                        //如果涉及中文
                        retValue = URLDecoder.decode(cookies[i].getValue(), "UTF-8");
                    } else {
                        retValue = cookies[i].getValue();
                    }
                    break;
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return retValue;
    }


    public static void setCookie(HttpServletRequest request, HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage, boolean isEncode) {
        try {
            if (cookieValue == null) {
                cookieValue = "";
            } else if (isEncode) {
                cookieValue = URLEncoder.encode(cookieValue, "utf-8");
            }
            Cookie cookie = new Cookie(cookieName, cookieValue);
            if (cookieMaxage >= 0) {
                cookie.setMaxAge(cookieMaxage);
            }
            if (null != request)// 设置域名的cookie
            {
                cookie.setDomain(getDomainName(request));
            }
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 得到cookie的域名
     */
    private static final String getDomainName(HttpServletRequest request) {
        String domainName = null;

        String serverName = request.getRequestURL().toString();
        if (serverName == null || serverName.equals("")) {
            domainName = "";
        } else {
            serverName = serverName.toLowerCase();
            serverName = serverName.substring(7);
            final int end = serverName.indexOf("/");
            serverName = serverName.substring(0, end);
            final String[] domains = serverName.split("\\.");
            int len = domains.length;
            if (len > 3) {
                // www.xxx.com.cn
                domainName = domains[len - 3] + "." + domains[len - 2] + "." + domains[len - 1];
            } else if (len <= 3 && len > 1) {
                // xxx.com or xxx.cn
                domainName = domains[len - 2] + "." + domains[len - 1];
            } else {
                domainName = serverName;
            }
        }

        if (domainName != null && domainName.indexOf(":") > 0) {
            String[] ary = domainName.split("\\:");
            domainName = ary[0];
        }
        System.out.println("domainName = " + domainName);
        return domainName;
    }

    public static void deleteCookie(HttpServletRequest request, HttpServletResponse response, String cookieName) {
        setCookie(request, response, cookieName, null, 0, false);
    }


}
```



#### WebConst常量类

```java
package com.wgml.itmall.util;

/**
 * @param
 * @return
 */
public class WebConst {

    public static final int COOKIE_MAXAGE = 7 * 24 * 3600;

    public static final String VERIFY_ADDRESS = "http://passport.itmall.com/verify";

    public static final String LOGIN_ADDRESS = "http://passport.itmall.com/login.html";


}
```

#### IpAddressUtil

```java
package com.wgml.itmall.util;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Ye Linfang
 * @date 2021/8/16 20:39
 * @description TODO
 * @since JDK1.8
 */
public class IpAddressUtil {
    /**
     * 获取Ip地址
     *
     * @param request
     * @return
     */
    public static String getIpAddress(HttpServletRequest request) {
        String Xip = request.getHeader("X-Real-IP");
        String XFor = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = XFor.indexOf(",");
            if (index != -1) {
                return XFor.substring(0, index);
            } else {
                return XFor;
            }
        }
        XFor = Xip;
        if (StringUtils.isNotEmpty(XFor) && !"unKnown".equalsIgnoreCase(XFor)) {
            return XFor;
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(XFor) || "unknown".equalsIgnoreCase(XFor)) {
            XFor = request.getRemoteAddr();
        }
        return XFor;
    }
}

```

#### UrlEncodeUtil

```java
package com.wgml.itmall.util;

import java.io.UnsupportedEncodingException;

/**
 * @author Ye Linfang
 * @date 2021/8/16 19:51
 * @description TODO
 * @since JDK1.8
 */
public class UrlEncodeUtil {

    public static String getURLEncoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        str = str.replaceAll("\\+", "%2B");
        try {
            result = java.net.URLEncoder.encode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String URLDecoderString(String str) {
        String result = "";
        if (null == str) {
            return "";
        }
        str = str.replaceAll("\\+", "%2B");
        try {
            result = java.net.URLDecoder.decode(str, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}

```

### 5 你好请登录，去登录页面js

```js
href="javascript:login();" 	

var login=function(){
			return location.href=" http://passport.itmall.com/login.html?originUrl="+escape(location.href).replace(/\//g,"%2F"),!1};
```

### 6 总结

```java
电商第十天：
	
	单点登录（SSO）：一处登录，处处可以使用！
		https://passport.jd.com
		cookie
		
	
	使用sso 前提条件：
		使用在分布式架构中！
		
	
	回顾登录的历史：
		单一架构：jsp+servlet+tomcat 书城
					登录之后，需要在页面显示用户名！
					将用户信息存储到 session 中！
		
		分布式架构：还能否将用户信息放入session中，
					有多个不同的服务器！
					不能直接将用户信息放入 session 中！
					
					请问应该将用户信息存放到何处？
					用户存放的信息，必须保证每个服务器都能访问到！ 
					无论哪个都能访问的点？ 缓存！redis中！
					
					
					
					
					面试题引出session 与 cookie 区别！
					
		单点登录的流程：
			登录的时候，用户Id ，token 去redis 缓存 认证！
			
			token: 令牌
			
			1.	当用户访问的业务模块，需要登录的时候，先去判断cookie中是否有token 
			2.	如果cookie 中有token ，无token ，
				2.1	无 token： 认为，没有登录： 则跳转到 登录，或者扫码页面！
				2.2	输入用户名，密码与后台数据库进行匹配！
					2.2.1	匹配正确生成token ，并放入cookie中。携带着 token ，继续访问其他业务模块！
					
				2.3	如果有token ，则继续访问业务！
			
			现实生活中的案例：
				逛园子！
					门票： token！
					认证中心：检票员
					动物园：
						1.	检票员：检查是否有门票
						2.	没有：买票==登录 。
						3.	再进行进门，检票员验证：有门票 = token
						4.	进入动物园！
					
	使用技术：
		cookie + redis + JWT + 拦截器 + 自定义注解 + httpClientUtils 
		
		
	认证中心模块搭建：https://passport.jd.com/
		认证用户是否登录：
		与数据库表：user_info 对比
		密码：加密暗文 202cb962ac59075b964b07152d234b70
		
		
		
		项目搭建：gmall-passport-web
			1.	pom.xml application.properties 静态文件
			
			2.	编写一个控制器测试页面
				
				
			3.	登录：
					功能开发：
						bean，mapper，service,service.impl,controller
						
					添加 redisUtil
					
					必须扫描，添加配置
					@ComponentScan(basePackages = "com.atguigu.gmall0218")
					#redis 配置放入
					spring.redis.host=192.168.67.219
					spring.redis.port=6379
					spring.redis.database=0
					
				
					登录成功之后，返回token 
						如何制作token ！ JWT 工具
						什么是JWT；一种基于JSON的开放标准
						JWT；组成：
							公共部分：自定义一个KEY=字符串
							私有部分：用户信息 {userId,nickName}
							签名部分：ip地址{服务器的} 192.168.67.1
								ip地址+KEY=字符串 = 组成一个生成 jwt 的key ！

							base64 编码： 将明文转化为暗文！
						JWT: 能防伪！
						
						使用步骤：
							导入jwt 依赖。
							
						JWT；实战
						
						在生成TOKEN 时，需要获取服务器的IP 地址
						在nginx.conf 中配置
						upstream passport.itmall.com{
							   server 192.168.72.1:8507;
							}
						   server {
							 listen 80;
							 server_name www.passport.itmall.com;
							 location / {
								proxy_pass http://passport.itmall.com;
								proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
							 }
							}
						编写hosts文件
						192.168.72.139 www.gmall.com item.gmall.com manage.gmall.com list.gmall.com passport.atguigu.com
						
						
				
			4.	测试登录：
				gmall-user_manage,gmall-passport-web
				
				登录顺序：
					https://www.jd.com/ 点击登录按钮
					https://passport.jd.com/new/login.aspx?ReturnUrl=https%3A%2F%2Fwww.jd.com%2F
																	 https%3A%2F%2Fwww.jd.com%2F
					登录成功之后则跳转到 https://www.jd.com/
					
				注意：在index 中要获取 originUrl
					originUrl : 用户在什么地方点击的登录的页面！
					
					登录成功之后：https://www.jd.com/?newToken=eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkF0Z3VpZ3UiLCJ1c2VySWQiOiIxIn0.XzRrXwDhYywUAFn-ICLJ9t3Xwz7RHo1VVwZZGNdKaaQ
					
					eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkF0Z3VpZ3UiLCJ1c2VySWQiOiIxIn0.XzRrXwDhYywUAFn-ICLJ9t3Xwz7RHo1VVwZZGNdKaaQ
					
				-------------------------------------------------------------------------
				登录：
					0.	什么是SSO? redis
					1.	根据用户输入的用户名，密码与数据库进行对比
					2.	true：需要将用户信息存入redis。
					3.	控制器在得到用户登录成功之后，返回token！
					
			---------------------------------------------------------------
					高能！
				下午：  将token 放入cookie ，
						并测试是否能够拦截用户的请求
					
				
				用户登录的认证：
					1.	获取服务器的Ip，token
					2.	key+ip ,解密token 得到用户的信息 userId,nickName 
					3.	判断用户是否登录：key=user:userId:info  value=userInfo 
					4.	userInfo!=null true; false;
					
					5.	测试认证：
						直接调用verify控制器
					http://passport.atguigu.com/verify?token=eyJhbGciOiJIUzI1NiJ9.eyJuaWNrTmFtZSI6IkF0Z3VpZ3UiLCJ1c2VySWQiOiIxIn0.XzRrXwDhYywUAFn-ICLJ9t3Xwz7RHo1VVwZZGNdKaaQ&salt=192.168.67.1
				
				在用户登录之后，将用户的昵称显示到页面上！
					用户昵称从何而来？
						通过解析token 得到！
						
					

					
					回顾：
					springmvx.xml
					<interceptors>
						// 拦截所有请求
						<interceptor class="com.atguigu.gmall0218.config.AuthInterceptor">
						</interceptor>
					</interceptors>
					
					
				测试步骤：
					1.	http://passport.atguigu.com/index?originUrl=http%3A%2F%2Fitem.gmall.com%2F36.html
					2.	输入用户名，密码。
					3.	web模块一定都要扫描包！
				
				测试用户访问的模块是否需要登录！
					需要做一个自定义注解：
					
					1. 	访问http://item.gmall.com/36.html 应该被拦截
						http://passport.atguigu.com/index?originUrl=http%3A%2F%2Fitem.gmall.com%2F36.html
					2.	登录之后，应该跳转到商品详情页面
						
						
				提示：
					setPath(); 设置cookie 访问路径
					setDoMain(); 设置cookie 访问的域名
					
	192.168.67.219 www.gmall.com item.gmall.com manage.gmall.com list.gmall.com passport.atguigu.com
				面试题：如何解决cookie 跨域存值问题！
					1.	第一次登录的时候，cookie 将信息 存储在 http://passport.atguigu.com 域名下
					2.	http://item.gmall.com 域名中如何或到用户昵称的？
						
					setPath(); 设置cookie 访问路径
					setDomain(); 设置cookie 访问的域名


```

#### Cookie的setPath(),和setDomain()方法

```java
正确使用cookie中的domain
domain的含义为域

假设有两个域名

a.b.e.f.com.cn  以下用域名1指代此域名

c.d.e.f.com.cn   以下用域名2指代此域名

在域名中，所有域名进行分级，也就是说域名1与域名2都是f.com.cn的子域名，f.com.cn又是com.cn的子域名

在域名1所使用的服务中，可以设置域名

a.b.e.f.com.cn

b.e.f.com.cn

e.f.com.cn

f.com.cn

在服务端设置domain的时候，设置domain为b.e.f.com.cn或.b.e.f.com.cn没有区别，注意前面的点，即只要是为cookie显式的声明domain，前面带不带点没有区别。这个点的奥妙后面还会提到。

设置其他域名虽然可以在响应头中有set-cookie的头，但是出于安全考虑，该头会被浏览器忽略，并不会产生真实的cookie，有一点注意，虽然在域名上来说，f.com.cn是com.cn的子域，但是浏览器是不会接收domain为com.cn的cookie的，那样互联网就乱套了。

对于域名1下的cookie，域名2可以拿到e.f.com.cn,f.com.cn这两个domain下的cookie，互联网上说的单点登录，就是以这个原理实现的。

如果存在相同名的cookie不同domain呢？

比如域名1设置设置domain为e.f.com.cn的cookie,mykey=myvalue1。

而在域名二中设置domain为c.d.e.f.com.cn的cookie，mykey=myvalue2。

此时在域名2服务端能拿到两个cookie都为mykey=myvalue，并不存在覆盖一说。

注意在域名2中如果设置domain为e.f.com.cn的cookie，mykey=myvalue3.此时会覆盖域为e.f.com.cn的mykey的值，即浏览器中，同一个域，只存在一个名为mykey的cookie。

    如果不显式设置cookie，默认当前域名这种说法对不对呢？ 

    先说第一个问题，如果不显示设置cookie，那么浏览器会生成一个只针对当前域名的cookie，什么叫只针对当前域名呢？

如果有一个域名就是e.f.com.cn，在该域名下设置的cookie如果没有显示domain，在回写浏览器的时候浏览器会特殊处理,如果是火狐，你会发现该cookie的信息，有一个主机项，而域项消失了，在chrome中，虽然有域这个项，但是域的前面少了一个点，就是上面所说的，如果显示声明，不管域中带不带点，到chrome中，都是带点存储的，只有非显式声明域的cookie，浏览器存储才是不带点的，问题就在这，在e.f.com.cn服务主机中生成的cookie，只有e.f.com.cn的服务器能拿到，此时子域名是拿不到这个cookie得。
原文链接：https://www.cnblogs.com/wenjia-hao/p/7365569.html    

========================================================================================================
    
“Cookies是一个很少的信息片段，可以自动地在浏览器与Web服务器间进行交互，因此Cookie可存储在多个页面间传递的信息”

正常的Cookie只能在一个应用中访问，即cookie只能由创建他的应用访问要实现跨应用访问cookie或者跨域访问cookie就得使用到cookie的setPath()和setDomain()方法。

1.同一服务器内的cookie共享方法：setPath()
eg:本机的tomcat/webapp下面有俩个应用webapp_a和webapp_b
1）webapp_a应用下设置的cookie，在webapp_b应用中是访问不到的，因为webapp_a的cookie默认的应用路径是webapp_a。
2）如果在webapp_a设置cookie时增加一条setPath(“/webapp_b/”)就能够在webapp_b中获取到webapp_a中所设置的cookie了。
3）要注意的是：在webapp_a的cookie中设置了setPath(“/webapp_b/”)，虽然是webapp_a应用产生的cookie但是作用路径是webapp_b,则webapp_a就不能获取cookie，只有webapp_b才能获取。
4）推而广之，如果在webapp_a设置cookie时增加一条setPath(“/”),即能实现webapp_a设置的cookie在webapp这个根目录下的共享。
5）ps：”/”指的是相对路径，本例中即是应用部署路径，tomcat/webapp。
6）以此类推，增加了setPath(“/webapp_b/jsp”)语句后，cookie只能在webapp_b/jsp目录下获取，同在webapp_b文件夹下的其他文件则不能获取。
7）某应用下设置cookie路径时，同时有多条setPath(“XXX”)语句时，最后一条语句才起作用。

2.跨域共享cookie的方法:setDomain()
eg:A机器所在域为：www.lizhiyu.com,有应用webapp_c
B机器所在域为: csdn.com，有应用webapp_d
1)在webapp_c设置cookie时，增加一条setDomain(“.csdn.com”),这样以后，在webapp_d下就能够取到webapp_c所产生的cookie了，同理，这样做了webapp_c就不能获取到他自己所产生的cookie了。
2）setDomain(“.xxx.xx”),这个方法必须以“.”开头。
3）输入url访问webapp_d时，必须url和在webapp_c中设置的url相同才能获取cookie。例如：在A机器上面输入url: http://csdn.com:8080/webapp_d,能够获取到webapp_c产生的cookie，但是在B机本地测试时候，输入url: http://localhost:8080/webapp_d，就不可以获取到cookie。
4) 设置了setDomain(“.csdn.com”)的cookie，在原来默认域下”www.lizhiyu.com”下共享。

问题两个：
？如何设置能够让A机器的www.lizhiyu.com和B 机器的csdn.com以及更多的域都能获取到www.lizhiyu.com所产生d的cookie？
？如何设置一个cookie，多条指定路径的方法还不清楚。？

以上就是我学习cookie所做的一些总结，学了的东西过了脑袋，再写篇博客整理思路，这样的方法还是能有效降低遗忘率。还剩下两个问题没有解决，希望有前辈指点一二。
————————————————
版权声明：本文为CSDN博主「MR_Peach07」的原创文章，遵循CC 4.0 BY-SA版权协议，转载请附上原文出处链接及本声明。
原文链接：https://blog.csdn.net/MR_Peach07/article/details/70442929
```



# 18 购物车

## 1 添加购物车

登录：redis+mysql

未登录：cookie

1、 根据**skuId**查询出商品详情skuInfo

2、 把skuInfo信息对应保存到购物车[购物车的实体类]

3、 返回成功页面



1、 先检查该用户的购物车里是否已经有该商品

2、 如果有商品，只要把对应商品的数量增加上去就可以，同时更新缓存

3、 如果没有该商品，则把对应商品插入到购物车中，同时插入缓存。

```java
 @Reference
    private ManageService manageService;
    @Autowired
    private CartInfoMapper cartInfoMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public void addToCart(String skuId, String userId, Integer skuNum) {
        /*
        1、	先检查该用户的购物车里是否已经有该商品
        2、	如果有商品，只要把对应商品的数量增加上去就可以，同时更新缓存
        3、	如果没有该商品，则把对应商品插入到购物车中，同时插入缓存。
        */
        Jedis jedis = null;
        try {
            //先检查该用户的购物车里是否已经有该商品
            CartInfo cartInfo = new CartInfo()
                    .setUserId(userId)
                    .setSkuId(skuId);
            CartInfo cartInfoExist = cartInfoMapper.selectOne(cartInfo);
            if (cartInfoExist != null) {
                //有商品把对应商品的数量增加上去,同时更新缓存
                cartInfoExist.setSkuNum(cartInfoExist.getSkuNum() + skuNum)
                        // 实时价格初始化
                        .setSkuPrice(cartInfoExist.getCartPrice())
                        .setUpdateTime(new Date());
                cartInfoMapper.updateByPrimaryKeySelective(cartInfoExist);
            } else {
                //没有该商品,根据skuid获取skuInfo，把对应商品插入到购物车中,同时插入缓存
                SkuInfo skuInfoByRedis = manageService.getSkuInfoByRedis(skuId);
                CartInfo cartInfo1 = new CartInfo()
                        .setSkuId(skuId)
                        .setCartPrice(skuInfoByRedis.getPrice())
                        .setSkuPrice(skuInfoByRedis.getPrice())
                        .setSkuName(skuInfoByRedis.getSkuName())
                        .setImgUrl(skuInfoByRedis.getSkuDefaultImg())
                        .setUserId(userId)
                        .setSkuNum(skuNum)
                        .setUpdateTime(new Date());
                //插入数据库
                cartInfoMapper.insertSelective(cartInfo1);
                cartInfoExist = cartInfo1;
            }

            //将购物车信息存进缓存，并设置缓存时间
            jedis = redisUtil.getJedis();
            // 构建key user:userid:cart
            String userCartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            jedis.hset(userCartKey, skuId, JSON.toJSONString(cartInfoExist));
            //设置缓存时间
            String userInfoKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USERINFOKEY_SUFFIX;
            Long ttl = jedis.ttl(userInfoKey);
            jedis.expire(userCartKey, ttl.intValue());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

    }
```

```java
package com.wgml.itmall.cart.constant;

/**
 * @author Ye Linfang
 * @date 2021/8/18 22:13
 * @description TODO
 * @since JDK1.8
 */
public class CartConst {
    public static final String USER_KEY_PREFIX = "user:";

    public static final String USER_CART_KEY_SUFFIX = ":cart";

    public static final String USER_CHECKED_KEY_SUFFIX = ":checked";

    public static final String USERINFOKEY_SUFFIX = ":info";

}
```



1、 获得参数：skuId 、skuNum

2、 判断该用户是否登录，用userId判断

3、 如果登录则调用后台的service的业务方法

4、 如果未登录，要把购物车信息暂存到cookie中。

```java
@Controller
public class CartController {
@Reference
    private CartService cartService;
    @Autowired
    private CartCookieHandler cartCookieHandler;
    @Reference
    private ManageService manageService;

    @RequestMapping("/addToCart")
    @LoginRequire(autoRedirect = false)  //没有登录不拦截
    public String addToCart(Integer skuNum, String skuId, HttpServletRequest request, HttpServletResponse response) {
        //获取拦截器中设置爱的userId
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            //用户登录添加购物车
            cartService.addToCart(skuId, userId, skuNum);
        } else {
            //用户没登录添加购物车 放到cookie中
            cartCookieHandler.addToCart(request, response, skuId, userId, skuNum);
        }

        SkuInfo skuInfo = manageService.getSkuInfoByRedis(skuId);
        request.setAttribute("skuInfo", skuInfo);
        request.setAttribute("skuNum", skuNum);
        return "success";
    }
```

实现利用cookie保存购物车的CartCookieHandler

和service模块的方法类似

1、 先查询出来在cookie中的购物车，反序列化成列表。

2、 通过循环比较有没有该商品

3、 如果有，增加数量

4、 如果没有，增加商品

5、 然后把列表反序列化，利用之前最好的CookieUtil保存到cookie中。

```java
@Component
public class CartCookieHandler {

    // 定义购物车名称
    private String cookieCartName = "CART";
    // 设置cookie 过期时间
    private int COOKIE_CART_MAXAGE = 7 * 24 * 3600;
    @Reference
    private ManageService manageService;


    /**
     * @param request
     * @param response
     * @param skuId
     * @param userId
     * @param skuNum
     * @return: void
     * @author Ye Linfang
     * @date 2021/8/18 23:06
     * @description 添加购物车到cookie 未登录的时候，添加到购物车
     */
    public void addToCart(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response, String skuId, String userId, Integer skuNum) {
        /*  1、	先查询出来在cookie中的购物车，反序列化成列表。
            2、	通过循环比较有没有该商品
            3、	如果有，增加数量
            4、	如果没有，增加商品
            5、	然后把列表反序列化，利用之前最好的CookieUtil保存到cookie中。*/

        //判断cookie中是否有购物车 有可能有中文，所有要进行序列化
        String cartJson = CookieUtil.getCookieValue(request, cookieCartName, true);
        List<CartInfo> cartInfoList = new ArrayList<>();
        boolean ifExist = false;
        if (StringUtils.isNotEmpty(cartJson)) {
            cartInfoList = JSON.parseArray(cartJson, CartInfo.class);
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getSkuId().equals(skuId)) {
                    // 更新数量，和实时价格初始化
                    cartInfo.setSkuNum(cartInfo.getSkuNum() + skuNum)
                            .setSkuPrice(cartInfo.getCartPrice())
                            .setUpdateTime(new Date());
                    //Cookie购物车中有该商品
                    ifExist = true;
                }
            }
        }
        // //购物车里没有对应的商品 或者 没有购物车
        if (!ifExist) {
            //把商品信息取出来，新增到购物车
            SkuInfo skuInfo = manageService.getSkuInfoByRedis(skuId);
            CartInfo cartInfo = new CartInfo()
                    .setSkuId(skuId)
                    .setCartPrice(skuInfo.getPrice())
                    .setSkuPrice(skuInfo.getPrice())
                    .setSkuName(skuInfo.getSkuName())
                    .setImgUrl(skuInfo.getSkuDefaultImg())
                    .setUserId(userId)
                    .setSkuNum(skuNum)
                    .setUpdateTime(new Date());
            cartInfoList.add(cartInfo);
        }
        // 把购物车写入cookie
        String newCartJson = JSON.toJSONString(cartInfoList);
        CookieUtil.setCookie(request, response, cookieCartName, newCartJson, COOKIE_CART_MAXAGE, true);


    }
```

## 2 功能—展示购物车列表

1、 展示购物中的信息

2、 如果**用户已登录**从缓存中取值，如果缓存没有，加载数据库。

3、 如果**用户未登录**从cookie中取值。

```java
   /**
     * @param request
     * @param response
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/19 11:54
     * @description 查看购物车列表
     */
    @RequestMapping("/cartList")
    @LoginRequire(autoRedirect = false)  //没有登录不拦截跳转
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        //获取拦截器中设置爱的userId
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (userId != null) {
            //用户登录,合并购物车,cookie中有则合并，无则从缓存或数据库中获取购物车数据
            List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
            if (cartListFromCookie != null && cartListFromCookie.size() > 0) {
                //开始合并
                cartInfoList = cartService.mergeToCartList(cartListFromCookie, userId);
                //合并完删除cookie中的购物车
                cartCookieHandler.deleteCartCookie(request, response);
            } else {
                //用户登录获取缓存或数据库购物车
                cartInfoList = cartService.getCartList(userId);
            }
        } else {
            //用户没登录查看cookie中的购物车
            cartInfoList = cartCookieHandler.getCartList(request);
        }

        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }
```

```java
注意点：
1、redis中取出来要进行反序列化
2、redis的hash结构是无序的，要进行排序（可以用时间戳或者主键id，倒序排序）
3、如果redis中没有要从数据库中查询，要连带把最新的价格也取出来，默认要显示最新价格而不是当时放入购物车的价格，如果考虑用户体验可以把两者的差价提示给用户。
4、加载入缓存时一定要设定失效时间，保证和用户信息的失效时间一致即可。
接口
  
@Override
    public List<CartInfo> getCartList(String userId) {
        List<CartInfo> cartInfoList = new ArrayList<CartInfo>();
        // 从redis中取得
        List<String> cartJsons = null;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            String userCartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            cartJsons = jedis.hvals(userCartKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        if (cartJsons != null && cartJsons.size() > 0) {
            for (String cartJson : cartJsons) {
                CartInfo cartInfo = JSON.parseObject(cartJson, CartInfo.class);
                cartInfoList.add(cartInfo);
            }
            //排序
            //按更新时间来排序
            cartInfoList.sort((o1, o2) -> {
                if (o2.getUpdateTime().getTime() - o1.getUpdateTime().getTime() > 0) {
                    return 1;
                } else {
                    return -1;
                }
            });
        } else {
            // 从数据库中查询，其中cart_price 可能是旧值，所以需要关联sku_info 表信息
            cartInfoList = loadCartCache(userId);
        }
        return cartInfoList;
    }

    @Override
    public List<CartInfo> loadCartCache(String userId) {
        List<CartInfo> cartInfoList = cartInfoMapper.selectCartListWithCurPrice(userId);
        if (cartInfoList == null || cartInfoList.size() == 0) {
            return null;
        }
        Map<String, String> map = new HashMap<>(cartInfoList.size());
        for (CartInfo cartInfo : cartInfoList) {
            // key 都是同一个，值会产生重复覆盖！
            map.put(cartInfo.getSkuId(), JSON.toJSONString(cartInfo));
        }
        String userCartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
        Jedis jedis = null;
        try {
            jedis = redisUtil.getJedis();
            jedis.hmset(userCartKey, map);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return cartInfoList;
    }
```

```java
//cartInfoMapper.selectCartListWithCurPrice(userId);接口
//.xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper SYSTEM "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.wgml.itmall.cart.mapper.CartInfoMapper">
    <resultMap id="cartMap" type="com.wgml.itmall.bean.cart.CartInfo" autoMapping="true">
        <id column="id" property="id"></id>
        <result column="price" property="skuPrice"></result>
    </resultMap>
    <select id="selectCartListWithCurPrice" resultMap="cartMap">
        SELECT ci.*, si.price
        FROM sku_info si
                 INNER JOIN cart_info ci
                            ON si.id = ci.sku_id
        WHERE ci.user_id = #{userId}
        order by update_time desc
    </select>
</mapper>    
            
//mybatis.mapper-locations=classpath:mapper/*Mapper.xml
//mybatis.configuration.mapUnderscoreToCamelCase=true

```

cookie得到购物车列表

```java
 /**
     * @param request
     * @return: java.util.List<com.wgml.itmall.bean.cart.CartInfo>
     * @author Ye Linfang
     * @date 2021/8/19 12:04
     * @description 未登录获取Cookie中的购物列表
     */
    public List<CartInfo> getCartList(HttpServletRequest request) {
        List<CartInfo> cartInfoList = null;
        String cookieValue = CookieUtil.getCookieValue(request, cookieCartName, true);
        if (StringUtils.isNotEmpty(cookieValue)) {
            cartInfoList = JSON.parseArray(cookieValue, CartInfo.class);
            //排序
            //按更新时间来排序
            cartInfoList.sort((o1, o2) -> {
                if (o2.getUpdateTime().getTime() - o1.getUpdateTime().getTime() > 0) {
                    return 1;
                } else {
                    return -1;
                }
            });
        }
        return cartInfoList;
    }
```



## 3 功能--合并购物车

由于加入购物车时，用户可能存在登录和未登录两种情况，登录前在cookie中保存了一部分购物车信息，如果用户登录了，那么对应的要把cookie中的购物车合并到数据库中，并且刷新缓存。

   思路：用数据库中的购物车列表与传递过来的cookie里的购物车列表循环匹配。

   能匹配上的数量相加

   匹配不上的插入到数据库中。

   最后重新加载缓存

```java
    @RequestMapping("/cartList")
    @LoginRequire(autoRedirect = false)  //没有登录不拦截跳转
    public String cartList(HttpServletRequest request, HttpServletResponse response) {
        //获取拦截器中设置爱的userId
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cartInfoList = new ArrayList<>();
        if (userId != null) {
            //用户登录,合并购物车,cookie中有则合并，无则从缓存或数据库中获取购物车数据
            List<CartInfo> cartListFromCookie = cartCookieHandler.getCartList(request);
            if (cartListFromCookie != null && cartListFromCookie.size() > 0) {
                //开始合并
                cartInfoList = cartService.mergeToCartList(cartListFromCookie, userId);
                //合并完删除cookie中的购物车
                cartCookieHandler.deleteCartCookie(request, response);
            } else {
                //用户登录获取缓存或数据库购物车
                cartInfoList = cartService.getCartList(userId);
            }
        } else {
            //用户没登录查看cookie中的购物车
            cartInfoList = cartCookieHandler.getCartList(request);
        }

        request.setAttribute("cartInfoList", cartInfoList);
        return "cartList";
    }
```

```java
//开始合并
@Override
    public List<CartInfo> mergeToCartList(List<CartInfo> cartListFromCookie, String userId) {
        List<CartInfo> cartInfoListDB = cartInfoMapper.selectCartListWithCurPrice(userId);

        for (CartInfo cartInfoCK : cartListFromCookie) {
            boolean isMatch = false;
            for (CartInfo cartInfoDB : cartInfoListDB) {
                if (cartInfoDB.getSkuId().equals(cartInfoCK.getSkuId())) {
                    //匹配上就更新数量
                    cartInfoDB.setSkuNum(cartInfoDB.getSkuNum() + cartInfoCK.getSkuNum())
                            //把cookie中时间更新给数据库
                            .setUpdateTime(cartInfoCK.getUpdateTime());
                    cartInfoMapper.updateByPrimaryKeySelective(cartInfoDB);
                    //匹配上更新状态
                    isMatch = true;
                }
            }
            if (!isMatch) {
                //没有匹配上则插入数据库中
                cartInfoCK.setUserId(userId);
                cartInfoMapper.insertSelective(cartInfoCK);
            }
        }
        //从数据库查询更新缓存
        List<CartInfo> cartInfoList = loadCartCache(userId);
        //修改合并后的数据选中状态，根据cookie中的选择状态来修改
        for (CartInfo cartInfoCK : cartListFromCookie) {
            for (CartInfo cartInfo : cartInfoList) {
                if (cartInfo.getSkuId().equals(cartInfoCK.getSkuId())) {
                    if ("1".equals(cartInfoCK.getIsChecked())) {
                        //修改返回数据选中状态
                        cartInfo.setIsChecked(cartInfoCK.getIsChecked());
                        //合并后，之前缓存的选择状态失效，修改缓存中的选择状态之前删除选中状态hash表中的所有数据
                        deleteCheckedCart(userId);
                        //修改缓存选中状态，并添加到新的选中缓存hash表中
                        checkCart(cartInfo.getSkuId(), "1", userId);
                    }
                }
            }
        }
        return cartInfoList;
    }
```

```java
//合并后删除cookie中的数据
public void deleteCartCookie(HttpServletRequest request, HttpServletResponse response) {
     CookieUtil.deleteCookie(request, response, cookieCartName);
}
```

```java
//删除选中缓存表中的购物车数据
@Override
    public void deleteCheckedCart(String userId) {
        Jedis jedis = null;
        try {
            String userCartCheckKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
            jedis = redisUtil.getJedis();
            jedis.del(userCartCheckKey);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }

```



## 4 选中状态的变更

 用户每次勾选购物车的多选框，都要把当前状态保存起来。由于可能会涉及更频繁的操作，所以这个勾选状态不必存储到数据库中。保留在缓存状态即可。

页面动作的js

```js
注意jquery1.6版本以后要用prop(“checked”)取得多选框选中状态。
function checkSku( chkbox){
   console.log($(chkbox));
   var skuId= $(chkbox).attr("value");
   var checked=$(chkbox).prop("checked");
   var isCheckedFlag="0";
   if(checked){
       isCheckedFlag="1";
   }
    var param="isChecked="+isCheckedFlag+"&"+"skuId="+skuId;
    console.log(param);
    $.post("checkCart",param,function (data) {
        sumSumPrice();
    });
}
```

```java
   @RequestMapping("/checkCart")
    @ResponseBody
    @LoginRequire(autoRedirect = false)  //没有登录不拦截跳转
    public void checkCart(HttpServletRequest request, HttpServletResponse response, String skuId, String isChecked) {
        //获取拦截器中设置爱的userId
        String userId = (String) request.getAttribute("userId");
        if (userId != null) {
            cartService.checkCart(skuId, isChecked, userId);
        } else {
            cartCookieHandler.checkCart(request, response, skuId, isChecked);
        }
    }
```

```java
把对应skuId的购物车的信息从redis中取出来，反序列化，修改isChecked标志。
再保存回redis中。
同时保存另一个redis的key 专门用来存储用户选中的商品，方便结算页面使用。
  
@Override
    public void checkCart(String skuId, String isChecked, String userId) {
        //根据用户选择状态isChecked，修改redis缓存中对应的购物车商品的状态，并根据userId和skuId和checked存储选中的购物车商品,删除未选中的
        Jedis jedis = null;
        try {
            String userCartKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CART_KEY_SUFFIX;
            jedis = redisUtil.getJedis();
              //取出来
            String userCartJson = jedis.hget(userCartKey, skuId);
            if (StringUtils.isNotEmpty(userCartJson)) {
                CartInfo cartInfo = JSON.parseObject(userCartJson, CartInfo.class);
                cartInfo.setIsChecked(isChecked);
                //放回去
                jedis.hset(userCartKey, skuId, JSON.toJSONString(cartInfo));
                String userCartCheckKey = CartConst.USER_KEY_PREFIX + userId + CartConst.USER_CHECKED_KEY_SUFFIX;
                if ("1".equals(isChecked)) {
                    //保存选中的商品
                    jedis.hset(userCartCheckKey, skuId, JSON.toJSONString(cartInfo));
                } else {
                    //删除未选中的商品
                    jedis.hdel(userCartCheckKey, skuId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
```

## 5点击结算要做的收尾工作

要解决用户在未登录且购物车中有商品的情况下，直接点击结算。

所以不能直接跳到结算页面，要让用户强制登录后，检查cookie并进行合并后再重定向到结算页面

```java
    /**
     * @return: java.lang.String
     * @author Ye Linfang
     * @date 2021/8/20 13:49
     * @description 点击结算要做的收尾工作 所以不能直接跳到结算页面，要让用户强制登录后，检查cookie并进行合并后再重定向到结算页面
     */
    @RequestMapping("toTrade")
    @LoginRequire(autoRedirect = true)
    public String toTrade(HttpServletRequest request, HttpServletResponse response) {
        String userId = (String) request.getAttribute("userId");
        List<CartInfo> cookieHandlerCartList = cartCookieHandler.getCartList(request);
        if (cookieHandlerCartList != null && cookieHandlerCartList.size() > 0) {
            cartService.mergeToCartList(cookieHandlerCartList, userId);
            //合并完删除
            cartCookieHandler.deleteCartCookie(request, response);
        }

        return "redirect://order.itmall.com/trade";
    }
```

## 总结

```java
电商第十一天：
	回顾：第十天 login
		注意：
			1.	index?originUrl=xxx
			2.	配置nginx 错了！ item.gmall.com  proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
			3.	包扫描！
			4.	注意注解添加
			5.	redis 启动 ./redis-server redis.cof
				
	制作购物车：
		基础：
					面向对象：
					根据面向对象思想设计一款购物车：
						每个用户的购物车的
						user:
							name,
							age,
							sex,
							
							addToCart(){add()};
						cart:
							id,
							name,
							image,
							num
							
							add();
							del();
							...
							
					功能实现？	
					--------------------------------------------------
						如何利用集合来实现一个购物车？
						采用哪个集合来保存购物车？
									集合框架：
											迭代器：
									collection 			    	Map
								List       Set    Queue   HashMap	TreeMap     
							ArrayList LinkedList        LinkedHashMap
						
						List , Set 区别？
							List 保存有序可重复，
							Set 无序不重复！
							
							Set 为什么无序不重复？
								hashcode();
								equals();
								
								先调用hashCode(); 算hash 值，
								equals(); 判断属性值
								
						ArryList LinkedList ，Vector区别？
							ArryList: 数组 查询快，修改快！
							LinkedList：链表，添加，删除快！
							Vector: 性能差，但是线程安全！
						
						TreeMap，HashMap 注意事项？
							TreeMap 添加key，value。
							
							HashMap 定义排序规则！
								comparable ，comparator 区别？
								comparable ：内部比较器，自然排序 {默认比较条件}
									compareTo(Studen stu){
										return this.id-stu.id;
									}
								comparator ：外部比较器，定制排序 {自己排序规则}
									comparable(Object o1,Object o2){
										
									}
						collection ，collections 区别？
							collection 接口
							collections 操作集合的工具类
							
						购物车的什么操作最频繁！
						
							优选：LinkedHashMap
		
	购物车简介：
		存储用户想要购买的商品。
		
		登录：
			添加，查询，修改，删除 购物车：
				将数据放入什么地方？
					讲：
						// redis + mysql {redis 缓存，mysql 数据持久化}
					
					面试：
						// redis
							redis 集群！高可用！
							rdb+aof 持久化！
						
			合并购物车：未登录+登录
			
				
		未登录：
			添加，查询，修改，删除 购物车：
				将数据放入什么地方？
					讲：
						cookie : 不涉及到跨域。。
					面试：
						redis : 不涉及到跨域。。
						
	1.	先制作项目：
		https://cart.jd.com 
		gmall-cart-web
		gmall-cart-service
		
		1.1	修改配置文件
		1.2	导入静态资源
		
		1.3	制作控制器
			根据在商品详情页点击加入购物车 的按钮 来确定控制器是谁！
			<form id="itemForm" action="http://cart.gmall.com/addToCart" method="post">
			
			修改 nginx.conf 
				 upstream cart.gmall.com{
				   server 192.168.67.1:8088;
				}
			   server {
				 listen 80;
				 server_name cart.gmall.com;
				 location / {
					proxy_pass http://cart.gmall.com;
					proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
				 }
				}

			1.3.1	测试
				gmall-manage-service gmall-item-web ,gmall-list-service gmall-cart-web 
				gmall-user-manage ,gmall-passport-web 
				
				
		1.4	由测试得出，添加购物车操作！并渲染页面！
		
			1.4.1	添加购物车：
					数据库表：cartInfo 
					bean, mapper,service,service.impl,controller
					
			1.4.2	添加购物车测试：
					
					登录：
						
					未登录：
				
			1.4.3	查看购物车列表
					http://cart.gmall.com/cartList
					
					sql： 
						SELECT * FROM sku_info si INNER JOIN cart_info ci ON si.id=ci.sku_id WHERE ci.user_id=1
			
			1.4.5	合并购物车
					
					未登录 --- 登录 
					
```



# 19 订单





# 20 支付





# 21 分布式事务



## 安装消息队列ActiveMQ

《day14 分布式事务》D:\桌面文件库\java资料\电商\笔记\课件



# 22 延迟队列与轮询拆单



# 其他

## 1、Maven和Spring Initializr项目区别



# 项目用Docker部署

docker容器镜像可视化portainer 安装命令

```shell
docker run -d -p 8066:9000 \
--restart=always -v /var/run/docker.sock:/var/run/docker.sock --privileged=true portainer/portainer
```



https://docs.docker.com/compose/

docker-compose up

docker-compose up --build

docker-compose up -d 后台启动

```yaml
version: '3.8'    #docker-compose版本
services:    
  redis:   #服务名 
    image: redis   #镜像名   也可以用build指定Dockerfile
    volumes:   #容器数据卷
      - myredis:/usr/local/redis/:rw
    ports:
      - "6379:6379"
    networks:  #docker网络
      - mynet

  mysql:
    image: mysql:5.7
    volumes:
      - mysql-data:/var/lib/mysql
      - mysql-conf:/etc/mysql/conf.d
    ports:
      - "3306:3306"
    networks:
      - mynet
    environment:
      - "MYSQL_ROOT_PASSWORD=txymysql123123"

  elasticsearch:
    image: elasticsearch:5.6.4
    ports:
      - "9200:9200"
      - "9300:9300"
    environment:
      - "discovery.type=single-node"
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    networks:
      - mynet
    volumes:
      - esdata:/etc/elasticsearch
      - eslog:/var/log/elasticsearch
  kibana:
    image: kibana:5.6.4
    ports:
      - "5601:5601"
    networks:
      - mynet
    depends_on:  #依赖启动
      - elasticsearch



volumes:
  mysql-data:
    driver: local
  mysql-conf:
    driver: local
  esdata:
    driver: local
  eslog:
    driver: local
  myredis:
    driver: local
networks:
  mynet:
    driver: bridge  #桥接方式
```

```shell
docker network ls
docker network inspect xxx_mynet
如果在同一个网络下，我们可以直接通过域名访问。
```

![image-20211021112852828](../../../../../ideaworkspaces/springboot-learning-note/docs/images/image-20211021112852828.png)

容器数据卷路径：**/var/lib/docker/voluems/xxx/_data**

tomcat:  **/home/tomcat**

停止命令：

```shell
docker-compose stop （在yaml文件目录执行才生效）
docker-compose down （停止并移除）
ctrl+c
```



## activemq

**activemq:**https://hub.docker.com/r/webcenter/activemq

```bash
docker pull webcenter/activemq:5.14.3
docker tag 镜像id oislfy/activemq:5.14.3
#docker-compose.yml
services:
  activemq:
    image: oislfy/activemq:5.14.3
    restart: always
    networks:
      - mynet
    ports:
      - "8161:8161"
      - "61616:61616"
      - "61613:61613"
    environment:
      - "ACTIVEMQ_ADMIN_LOGIN=oislfy"
      - "ACTIVEMQ_ADMIN_PASSWORD=txyactivemq"
      - "ACTIVEMQ_CONFIG_MINMEMORY=512"
      - "ACTIVEMQ_CONFIG_MAXMEMORY=512"
    volumes:
      - activemqdata:/data
      - activemqlog:/var/log/activemq
```



## nginx、首页配置

```shell
  server {
          listen       80;
          server_name  www.itaomall.com;
          location / {
                root front;
                index index.html;
                }
        }
    upstream item.itmall.com{
            server 192.168.72.1:8504;
        }

    server {
          listen       80;
          server_name  item.itmall.com;
          location / {
                proxy_pass http://item.itmall.com;
		proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
                }
        }
	
	upstream manage.itmall.com {
                server 192.168.72.1:8502;
        }
    	server {
                listen       80;
                server_name  manage.itmall.com;
                location /{
                        proxy_pass http://manage.itmall.com;
			proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
                }
        }

    	upstream list.itmall.com{
                server 192.168.72.1:8506;
        }
        server {
                listen       80;
                server_name  list.itmall.com;
                location /{
                        proxy_pass http://list.itmall.com;
			proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
                }
        }
	upstream passport.itmall.com{
		server 192.168.72.1:8507;
		}
	 server {
		 listen 80;
		 server_name passport.itmall.com;
		location / {
			proxy_pass http://passport.itmall.com;
			proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
				 }
		}
	    upstream cart.itmall.com{
	       server 192.168.72.1:8508;
	    }
  	 server {
  	   listen 80;
     	server_name cart.itmall.com;
     	location / {
       		 proxy_pass http://cart.itmall.com;
       		 proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
    		 }
  	  }

	    upstream order.itmall.com{
      		 server 192.168.72.1:8501;
	    }
	   server {
     		listen 80;
    		 server_name order.itmall.com;
    		 location / {
       			 proxy_pass http://order.itmall.com;
       			 proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
     		}
   	 }


	upstream www.itaoware.com{
        server 192.168.72.1:9001;
    }
    server {
        listen       80;
        server_name  www.itaoware.com;
        location / {
               proxy_pass http://www.itaoware.com; 
        }      
      }  

	upstream payment.itmall.com{
       server 192.168.72.1:8511;
    }
   server {
     listen 80;
     server_name payment.itmall.com;
     location / {
        proxy_pass http://payment.itmall.com;
        proxy_set_header X-forwarded-for $proxy_add_x_forwarded_for;
     }
    }


    server {
        listen       80;
        server_name  192.168.72.139;

        #charset koi8-r;

        #access_log  logs/host.access.log  main;

        location / {
            root   html;
            index  index.html index.htm;
        }
        location /group1/M00/ {
             ngx_fastdfs_module;
        }
```



## 过程：

### 1、修改配置文件为生产配置（同一个docker网络下可以用服务名代替ip地址）

### 2、maven打jar包（先clean再package）

### 3、写对应微服务的Dockerfile文件

```text
FROM java:8
COPY ./jar/itmall-cart-service-0.0.1-SNAPSHOT.jar /app.jar
ENV MYPATH  /usr/local
WORKDIR $MYPATH
CMD ["--server.port=8509"]
EXPOSE 8509
ENTRYPOINT ["java","-jar","/app.jar","--spring.profiles.active=prod","> ./log/mylog.log &"]
```



### 4、写docker-compose.yml文件

```yml
version: '3.8'
services:

  redis:
    image: redis:3.2.8
    volumes:
      - myredis:/usr/local/redis/:rw
    ports:
      - "6379:6379"
    networks:
      - mynet
    restart: always

  mysql:
    image: mysql:5.7
    volumes:
      - mysql-data:/var/lib/mysql
      - mysql-conf:/etc/mysql/conf.d
    ports:
      - "3306:3306"
    networks:
      - mynet
    environment:
      - "MYSQL_ROOT_PASSWORD=txymysql123123"
    restart: always

  elasticsearch:
    image: elasticsearch:5.6.4
    ports:
      - "9200:9200"
      - "9300:9300"
    environment:
      - "discovery.type=single-node"
      - "ES_JAVA_OPTS=-Xms512m -Xmx512m"
    networks:
      - mynet
    volumes:
      - esdata:/etc/elasticsearch
      - eslog:/var/log/elasticsearch
    restart: always

  kibana:
    image: kibana:5.6.4
    ports:
      - "5601:5601"
    networks:
      - mynet
    depends_on:
      - elasticsearch
    restart: always

  zookeeper:
    image: zookeeper:3.4.11
    ports:
      - "2181:2181"
    restart: always
    volumes:
      - myzookeeper:/conf/:rw
    networks:
      - mynet

  tomcat:
    image: tomcat:8.5
    ports:
      - "8078:8080"
    volumes:
      - mytomcat:/usr/local/tomcat/:rw
    restart: always
    networks:
      - mynet

  activemq:
    image: oislfy/activemq:5.14.3
    restart: always
    networks:
      - mynet
    ports:
      - "8161:8161"
      - "61616:61616"
      - "61613:61613"
    environment:
      - "ACTIVEMQ_ADMIN_LOGIN=oislfy"
      - "ACTIVEMQ_ADMIN_PASSWORD=txyactivemq"
      - "ACTIVEMQ_CONFIG_MINMEMORY=512"
      - "ACTIVEMQ_CONFIG_MAXMEMORY=512"
    volumes:
      - activemqdata:/data
      - activemqlog:/var/log/activemq


  nginx:
    image: nginx
    restart: always
    networks:
      - mynet
    ports:
      - "80:80"
    volumes:
      - mynginxdata:/usr/share/nginx/:rw
      - mynginxconf:/etc/nginx/:rw

  manage-service:
    build:
      context: ./
      dockerfile: Dockerfile-manage-s
    restart: always
    networks:
      - mynet
    ports:
      - "8503:8503"
    volumes:
      - manage-service:/usr/local/log/:rw
    depends_on:
      - mysql
      - zookeeper
      - elasticsearch
      - activemq
      - nginx

  manage-web:
    build:
      context: ./
      dockerfile: Dockerfile-manage-w
    restart: always
    networks:
      - mynet
    ports:
      - "8502:8502"
    volumes:
      - manage-web:/usr/local/log/:rw
    depends_on:
      - manage-service

  cart-service:
    build:
      context: ./
      dockerfile: Dockerfile-cart-s
    restart: always
    networks:
      - mynet
    ports:
      - "8509:8509"
    volumes:
      - cart-service:/usr/local/log/:rw
    depends_on:
      - manage-service

  cart-web:
    build:
      context: ./
      dockerfile: Dockerfile-cart-w
    restart: always
    networks:
      - mynet
    ports:
      - "8508:8508"
    volumes:
      - cart-web:/usr/local/log/:rw
    depends_on:
      - cart-service

  itaoware:
    build:
      context: ./
      dockerfile: Dockerfile-itaoware
    restart: always
    networks:
      - mynet
    ports:
      - "9001:9001"
    volumes:
      - itaoware:/usr/local/log/:rw
    depends_on:
      - manage-service


  item-web:
    build:
      context: ./
      dockerfile: Dockerfile-item-w
    restart: always
    networks:
      - mynet
    ports:
      - "8504:8504"
    volumes:
      - item-web:/usr/local/log/:rw
    depends_on:
      - manage-service

  list-service:
    build:
      context: ./
      dockerfile: Dockerfile-list-s
    restart: always
    networks:
      - mynet
    ports:
      - "8505:8505"
    volumes:
      - list-service:/usr/local/log/:rw
    depends_on:
      - manage-service

  list-web:
    build:
      context: ./
      dockerfile: Dockerfile-list-w
    restart: always
    networks:
      - mynet
    ports:
      - "8506:8506"
    volumes:
      - list-web:/usr/local/log/:rw
    depends_on:
      - list-service

  order-service:
    build:
      context: ./
      dockerfile: Dockerfile-order-s
    restart: always
    networks:
      - mynet
    ports:
      - "8510:8510"
    volumes:
      - order-service:/usr/local/log/:rw
    depends_on:
      - manage-service

  order-web:
    build:
      context: ./
      dockerfile: Dockerfile-order-w
    restart: always
    networks:
      - mynet
    ports:
      - "8501:8501"
    volumes:
      - order-web:/usr/local/log/:rw
    depends_on:
      - order-service

  passport-web:
    build:
      context: ./
      dockerfile: Dockerfile-passport-w
    restart: always
    networks:
      - mynet
    ports:
      - "8507:8507"
    volumes:
      - passport-web:/usr/local/log/:rw
    depends_on:
      - manage-service

  payment:
    build:
      context: ./
      dockerfile: Dockerfile-payment
    restart: always
    networks:
      - mynet
    ports:
      - "8511:8511"
    volumes:
      - payment:/usr/local/log/:rw
    depends_on:
      - manage-service

  user-service:
    build:
      context: ./
      dockerfile: Dockerfile-user-s
    restart: always
    networks:
      - mynet
    ports:
      - "8500:8500"
    volumes:
      - user-service:/usr/local/log/:rw
    depends_on:
      - manage-service

volumes:
  mysql-data:
    driver: local
  mysql-conf:
    driver: local
  esdata:
    driver: local
  eslog:
    driver: local
  myredis:
    driver: local
  myzookeeper:
    driver: local
  mytomcat:
    driver: local
  activemqdata:
    driver: local
  activemqlog:
    driver: local
  mynginxdata:
    driver: local
  mynginxconf:
    driver: local
  manage-service:
    driver: local
  manage-web:
    driver: local
  cart-service:
    driver: local
  cart-web:
    driver: local
  itaoware:
    driver: local
  item-web:
    driver: local
  list-service:
    driver: local
  list-web:
    driver: local
  order-service:
    driver: local
  order-web:
    driver: local
  passport-web:
    driver: local
  payment:
    driver: local
  user-service:
    driver: local


networks:
  mynet:
    driver: bridge


```



### 5、上传所有文件到服务器，用xftp工具



<img src="../../../../../ideaworkspaces/springboot-learning-note/docs/images/image-20211022183632919.png" alt="image-20211022183632919" style="zoom:50%;" />

### 6、修改文件权限 chmod 777 *.jar

### 7、执行docker-compose.yml文件

```shell
docker-compose up -f 文件名 --build -d
```

### 8、商品上架 manage.itmall.com/onSale?skuId=34

elasticsearch  解压安装中文分词

```shell
docker cp /home/elasticsearch e74b42ef6e3b:/usr/share/elasticsearch/plugins
#重启容器
```

使用Kibana  建立数据库 

```json
PUT itaomall
{
  "mappings": {
    "SkuInfo":{
      "properties": {
        "id":{
          "type": "keyword"
          , "index": false
        },
        "price":{
          "type": "double"
        },
         "skuName":{
          "type": "text",
          "analyzer": "ik_max_word"
        },
        "catalog3Id":{
          "type": "keyword"
        },
        "skuDefaultImg":{
          "type": "keyword",
          "index": false
        },
        "skuAttrValueList":{
          "properties": {
            "valueId":{
              "type":"keyword"
            }
          }
        },
        "valuesSkuJson":{
          "type": "keyword"
        }
      }
    }
  }
}
```





<img src="../../../../../ideaworkspaces/springboot-learning-note/docs/images/image-20211022192821207.png" alt="image-20211022192821207" style="zoom:80%;" />





## 部署异常：

no main manifest attribute, in /app.jar  

```java
//打包问题
<build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

```

runtime out of memory 运行内存不够





