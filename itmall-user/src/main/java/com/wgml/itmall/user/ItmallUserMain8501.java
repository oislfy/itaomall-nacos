package com.wgml.itmall.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author oislfy
 * @date 2021-11-08 0:21
 */

//@EnableActiveMQ
@EnableFeignClients(basePackages = "com.wgml.itmall")
@EnableDiscoveryClient
@SpringBootApplication
@ComponentScan(basePackages = "com.wgml.itmall")
public class ItmallUserMain8501 {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ItmallUserMain8501.class, args);
    }
}
