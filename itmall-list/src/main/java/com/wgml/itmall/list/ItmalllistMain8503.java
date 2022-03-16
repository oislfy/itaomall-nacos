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
@ComponentScan(basePackages = "com.wgml.itmall") //扫描swagger2配置、RedisUtil等
public class ItmalllistMain8503 {
    public static void main(String[] args) {
        SpringApplication.run(ItmalllistMain8503.class, args);
    }
}

