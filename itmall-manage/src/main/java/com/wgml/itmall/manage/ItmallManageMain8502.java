package com.wgml.itmall.manage;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 * @author oislfy
 * @date 2021-11-07 16:19
 */
//@EnableActiveMQ
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.wgml.itmall")
@EnableTransactionManagement   //开启事务管理
@MapperScan(basePackages = "com.wgml.itmall.manage.mapper")
@ComponentScan(basePackages = "com.wgml.itmall")
public class ItmallManageMain8502 {
    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ItmallManageMain8502.class, args);
    }

    /**
     * 设置时区
     */

    @PostConstruct
    void setDefaultTimezone() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
    }
}
