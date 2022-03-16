package com.wgml.itmall.config;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * @author oislfy
 */
@EnableSwagger2
@Configuration
@RefreshScope
public class Swagger2Config {

    @Value("${swagger2.enabled:false}")
    private boolean enabled = false;

    @Bean
    public Docket webApiConfig() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
//                .paths(PathSelectors.regex("/project.*"))
                .paths(PathSelectors.regex("/.*"))
                .build()
                .apiInfo(webApiInfo())
                .enable(enabled);

    }

    private ApiInfo webApiInfo() {

        return new ApiInfoBuilder()
                .title("爱淘商城-API文档")
                .description("本文档描述了爱淘商城接口定义")
                .version("1.0")
                .contact(new Contact("oislfy", "http://itmall.site", "oislfy@qq.com"))
                .termsOfServiceUrl("http://itmall.site")
                .build();
    }

}