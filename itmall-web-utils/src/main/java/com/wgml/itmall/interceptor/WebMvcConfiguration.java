package com.wgml.itmall.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Ye Linfang
 * 配置完拦截器之后，需要在web项目中添加包扫描
 * @Configuration public class WebMvcConfiguration extends WebMvcConfigurerAdapter  旧版
 */
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    @Resource
    private AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加拦截器
        //静态资源放行
        List<String> excludePaths = new ArrayList<>();
        excludePaths.add("/js/**");
        excludePaths.add("/images/**");
        excludePaths.add("/css/**");
        excludePaths.add("/fonts/**");
        registry.addInterceptor(authInterceptor).addPathPatterns("/**").excludePathPatterns(excludePaths);
        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
