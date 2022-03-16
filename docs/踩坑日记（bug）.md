# 1、springcloud调用失败问题：

nacos注册中心，服务注册成功后，其他配置没有问题，使用openFeign调用失败，走了feign的fallback的方法问题！

```java
@FeignClient(value = "itmall-manage",fallback = ManageFallbackHandler.class)
public interface ManageServiceFeign {
    /*
     *  获取3级分类
     */
    @GetMapping("/getCatalog3")
    public R getCatalog3(@RequestParam String catalog2Id);
}
```

原因：feign接口方法参数注解@RequestParam未指定参数名

修改后：

```java
 @GetMapping("/getCatalog3")
  public R getCatalog3(@RequestParam("catalog2Id") String catalog2Id);
```

原因二：主启动类缺少包扫描，@FeignClient(value = "itmall-manage",fallback = ManageFallbackHandler.class) 此注解识别不了

```java
@EnableFeignClients(basePackages = "com.wgml.itmall")
```





# 2、pom问题

未依赖父工程导致项目版本错乱，项目启动失败



# 3、sentinel的资源名问题

```java
   @GetMapping("/info")
    @SentinelResource(value = "info",
            blockHandlerClass = MyHanderBlock.class,
            blockHandler = "testHandler")
    public String test() {
        return info;
    }
```

如果规则中有==/info==和==info==资源名规则，则先==/info==资源名的规则为先生效，==info==资源名不生效



# 4、required a single bean, but 2 were found:

Description: 自动配置类中要求一个bean，但是找到了两个

Parameter 0 of method elasticsearchTemplate in org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataConfiguration$RestClientConfiguration required a single bean, but 2 were found:
	- esRestClient: defined by method 'restHighLevelClient' in class path resource [com/wgml/itmall/list/config/ElasticSearchConfig.class]
	- elasticsearchRestHighLevelClient: defined by method 'elasticsearchRestHighLevelClient' in class path resource [org/springframework/boot/autoconfigure/elasticsearch/ElasticsearchRestClientConfigurations$RestHighLevelClientConfiguration.class]


Action:

Consider marking one of the beans as @Primary, updating the consumer to accept multiple beans, or using @Qualifier to identify the bean that should be consumed

**疑似产生的原因**
同名

**弯路、坑**
**分析**
没有指定BeanName，Spring使用了默认值，导致重名而不能启动

**解决方案**
	在注解增加BeanName配置：@Bean(value="beanName")和`@Primary`,带`@Primary`注解的优先，表示是主Bean。消费者使用@Qualifier(value="beanName")消费Bean



# 5、BeanCreationException RequestParam.value() was empty on parameter 0

问题：org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'listController': Injection of resource dependencies failed; nested exception is org.springframework.beans.factory.BeanCreationException: Error creating bean with name 'com.wgml.itmall.service.manage.ManageServiceFeign': FactoryBean threw exception on object creation; nested exception is java.lang.IllegalStateException: RequestParam.value() was empty on parameter 0

分析：ManageServiceFeign openfeign调用接口类@FeignClient 中@RequestParam注解需要添加value  

解决：@RequestParam改为@RequestParam("skuId")

```java
 @GetMapping("/manage-service/getSkuLsInfo")
    public SkuLsInfo getSkuLsInfo(@RequestParam("skuId") String skuId);
```



# 6、Can't retrieve image ID from build stream

Failed to deploy 'registry.cn-guangzhou.aliyuncs.com/wugumaolu-oislfy/itmall-manage Dockerfile: itmall-manage/src/main/resources/Dockerfile': Can't retrieve image ID from build stream

解决：

```dockerfile
FROM java:1.8
VOLUME /tmp
ADD ./itmall-manage-1.0-SNAPSHOT.jar /app.jar
EXPOSE 8502
ENTRYPOINT ["java","-Xms256m","-Xmx256m","-jar","/app.jar"]
```

将 **FROM java:1.8** 改为 **FROM java:8**

