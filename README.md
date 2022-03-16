# 爱淘商城-nacos注册中心版

# 1、版本选取和参照：

alibaba官方地址：https://github.com/alibaba/spring-cloud-alibaba/wiki/%E7%89%88%E6%9C%AC%E8%AF%B4%E6%98%8E

| 组件名称                    | 依赖版本             |
| --------------------------- | -------------------- |
| Jdk                         | 1.8                  |
| Maven                       | 3.5.0                |
| SpringBoot                  | 2.3.3.RELEASE        |
| SpringCloud                 | Hoxton.SR9           |
| SpringCloudAlibaba          | 2.2.6.RELEASE        |
| nacos                       | 1.4.2                |
| sentinel                    | 1.8.2                |
| redis                       | 6.2.1                |
| elasticsearch               | 7.6.2                |
| mapper-spring-boot-starter  | 2.1.5                |
| mybatis-spring-boot-starter | 1.3.1                |
| mysql                       | 5.7                  |
| nginx                       | 1.20.1               |
| activemq                    | 5.14.3               |
| 其他                        | 参考父工程properties |

需求

项目描述

# 2、项目架构搭建-parent.pom



# itmall-manage模块









# itmall-list模块

## 新建Module：itmall-list

## es准备

**操作前安装es，kibana，安装ik中文分词器并设置索引库的mapping 数据结构**

> docker-compose安装es、kibana        ELK:Elasticsearch , Logstash, Kibana

**docker-compose.yaml**

```yaml
version: "3"
services:
  elasticsearch:
    image: elasticsearch:7.6.2
    ports:
      - "9200:9200"
      - "9300:9300"
    env_file:
      - ./env/elasticsearch.env
    networks:
      - itmall_nacos_net
    volumes:
      - ./es/plugins:/usr/share/elasticsearch/plugins
    restart: always

  kibana:
    image: kibana:7.6.2
    ports:
      - "5601:5601"
    networks:
      - itmall_nacos_net
    depends_on:
      - elasticsearch
    restart: always	
```

elasticsearch.env

```
discovery.type=single-node
ES_JAVA_OPTS=-Xms512m -Xmx512m
```

> 安装ik中文分词器

把elasticsearch-analysis-ik-7.6.1.zip解压放进./es/plugins文件夹中

> 根据业务搭建数据结构/建立mapping

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

==Index：itaomall==

==type：_doc==  //7.6版本以上慢慢抛弃type 默认为  _doc

==document: properties - rows==

==field: id,price,skuName…==

Es中index默认是true。 该字段是否用来检索

kibana-[Dev Tools](http://192.168.72.139:5601/app/kibana#/dev_tools)里执行如下：

```json
PUT itaomall
{
  "mappings": {
    
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
//删除索引库
DELETE itaomall

GET /itaomall/_doc/_search

//结果：
{
  "acknowledged": true,
  "shards_acknowledged": true,
  "index": "itaomall"
}
```

考虑：

保存的时候怎么分词规则：mapping定义

搜索的时候采用怎样分词

## POM

```xml
 <groupId>com.wgml.nacos.itmall</groupId>
<artifactId>itmall-list</artifactId>
<version>1.0-SNAPSHOT</version> 
<parent>
        <groupId>com.wgml.nacos</groupId>
        <artifactId>itmall-parent</artifactId>
        <version>1.0-SNAPSHOT</version>
        <relativePath>../itmall-parent/pom.xml</relativePath>
</parent>
 <dependencies>
        <dependency>
            <groupId>com.wgml.nacos</groupId>
            <artifactId>itmall-service-utils</artifactId>
            <version>${parent.version}</version>
            <!--排除不需要的依赖-->
            <!--排除不需要的依赖-->
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-activemq</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.apache.activemq</groupId>
                    <artifactId>activemq-pool</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-jdbc</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>org.mybatis.spring.boot</groupId>
                    <artifactId>mybatis-spring-boot-starter</artifactId>
                </exclusion>
                <exclusion>
                    <groupId>mysql</groupId>
                    <artifactId>mysql-connector-java</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>com.wgml.nacos</groupId>
            <artifactId>itmall-common-api</artifactId>
            <version>${parent.version}</version>
        </dependency>

        <!--        elasticsearch-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-elasticsearch</artifactId>
        </dependency>
   </dependencies>
        <!-- jsoup解析页面 -->
        <!-- 解析网页 爬视频可 研究tiko -->
    <dependency>
        <groupId>org.jsoup</groupId>
        <artifactId>jsoup</artifactId>
    </dependency>
```

## YAML配置

**bootstrap.yaml**

```yaml
server:
  port: 8503

spring:
  application:
    name: itmall-list
  cloud:
    nacos:
      discovery:
        #Nacos服务注册中心地址
        server-addr: 192.168.72.139:8848
        namespace: dev-id
      config:
        server-addr: 192.168.72.139:8848 #Nacos作为配置中心地址
        file-extension: yaml #指定yaml格式的配置
        namespace: dev-id
        group: DEV_GROUP
    sentinel:
      transport:
        #配置Sentinel dashboard地址
        dashboard: 192.168.72.139:8877
        #默认8719端口，假如被占用会自动从8719开始依次+1扫描,直至找到未被占用的端口
        port: 8719
        client-ip: 192.168.72.1
      datasource:
        #1.8+版本sentinel
        flow:
          nacos:
            server-addr: 192.168.72.139:8848
            dataId: ${spring.application.name}-flow-rules
            namespace: ${spring.cloud.nacos.discovery.namespace}
            groupId: DEV_GROUP
            data-type: json
            rule-type: flow
          #1.7.0sentinel
#        ds1:
#          nacos:
#            server-addr: 192.168.72.139:8848
#            dataId: itmall-manage-sentinel
#            namespaceId: dev-id
#            groupId: DEV_GROUP
#            data-type: json
#            rule-type: flow



feign:
  hystrix:
    enabled: false #默认，可以不写 禁用feign的hystrix功能，改用sentinel的
  sentinel:
    enabled: true   #激活Sentinel对Feign的支持,熔断降级
ribbon:
  ReadTimeout: 5000
  ConnectTimeout: 5000

#暴露端点 sentinel nacos 用到
management:
  endpoints:
    web:
      exposure:
        include: '*'
```

**application.yaml**

```yaml
spring:
  profiles:
    active: dev
  redis:
    host: 192.168.72.139
    port: 6380
    database: 0
    timeout: 1800000ms
    password: itmallredis
    lettuce:
      pool:
        max-active: 20
        max-wait: -1ms
        max-idle: 10
        min-idle: 0
#此模块不需要数据源
#  datasource:
#    url: jdbc:mysql://192.168.72.139:3330/itaomall?characterEncoding=UTF-8&useSSL=false
#    username: root
#    password: rootitmall
#    driver-class-name: com.mysql.cj.jdbc.Driver
#  elasticsearch:   //这里用自己配置的高级rset客户端，暂不用springboot自动配置的
#      jest:
#        uris: http://1.12.223.197:9200
#      rest:
#        uris: http://1.12.223.197:9200

swagger2:
  enabled: true  #开启swagger2
#自定义es高级rest客户端RestHighLevelClient
es:
  host: 192.168.72.139
  port: 9200

```

## 主启动类

**ItmalllistMain8503**

```java
package com.wgml.itmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author oislfy
 * @date 2021-11-09 17:14
 */
@SpringBootApplication
@EnableDiscoveryClient  //开启服务发现，nacos注册中心
@EnableFeignClients(basePackages = "com.wgml.itmall") //开启FeignClient并扫描标@FeignClient的类
@ComponentScan(basePackages = "com.wgml.itmall") //扫描swagger2配置、RedisUtil工具类等
public class ItmalllistMain8503 {
    public static void main(String[] args) {
        SpringApplication.run(ItmalllistMain8503.class, args);
    }
}
```

## 业务类

### 配置类

**ElasticSearchConfig**

```java
package com.wgml.itmall.list.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
/**
 * @author oislfy
 * @date 2021-11-12 20:57
 */
@Configuration
@RefreshScope
public class ElasticSearchConfig {

    @Value("${es.host:localhost}")
    private String host;
    @Value("${es.port:9200}")
    private int port;

//    @Bean(value = "esRestClient")

    @Bean
    @Primary //指定默认注入类
    @ConditionalOnProperty(prefix = "es", name = {"host", "port"}, matchIfMissing = false)
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, "http")));
        return client;
    }
}

```



### 控制层

**ListController**

```java
/**
 * @author oislfy
 * @date 2021-11-09 18:45
 */
@RestController
@Api(tags = "商品列表API接口文档")
public class ListController {
    @Resource(name = "listServiceImpl")
    private ListService listService;

    @ApiOperation("保存skuLs到es")
    @PostMapping("/saveSkuLsInfo")
    public Boolean saveSkuLsInfo(@RequestBody SkuLsInfo skuLsInfo) {
        return listService.saveSkuLsInfo(skuLsInfo);
    }
    
    
}
```



### 接口服务+业务实现

**ListService**

```java
/**
 * @author oislfy
 */
public interface ListService {
    Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo);
    
}
```

**ListServiceImpl**

```java
/**
 * @author oislfy
 */
@Slf4j
@Service
@RefreshScope
public class ListServiceImpl implements ListService {
    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient esClient;

	@Value("${es.esIndex:itaomall}")
    private String esIndex;
    
    @Resource
    private RedisUtil redisUtil;

    @Override
    public Boolean saveSkuLsInfo(SkuLsInfo skuLsInfo) {
        // 保存数据 保存前设置索引库的mapping 数据结构
        try {
            IndexResponse response = esClient.index(
                new IndexRequest(esIndex)
                    .id(skuLsInfo.getId())
                    .timeout(TimeValue.timeValueSeconds(1))
                    .source(JSON.toJSONString(skuLsInfo), XContentType.JSON), 		       RequestOptions.DEFAULT); 
            return response.isFragment(); //是否保存
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
```







技术总结：

docker docker-compose  阿里云镜像服务

rabbitmq

springboot 全家桶

springcloud全家桶

springcloudalibaba全家桶

nginx

swagger3 [ knife4j](https://doc.xiaominfo.com/knife4j/)

中间件









































