package com.wgml.itmall.manage.constant;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

/**
 * @author Ye Linfang
 * @date 2021/8/10 13:39
 * @description 常量类
 * @since JDK1.8
 */
@Component
@RefreshScope
public class ManageConst implements InitializingBean {
    public static final String SKUKEY_PREFIX="sku:";

    public static final String SKUKEY_SUFFIX=":info";
    //一天
    public static final long SKUKEY_TIMEOUT=24*60*60;
    //10秒
    public static final long SKULOCK_EXPIRE_PX=10;

    public static final String SKULOCK_SUFFIX=":lock";

    @Value("${spring.redis.host}")
    private String redis_host;

    @Value("${spring.redis.port}")
    private String redis_port;

//    public static final String REDISSION_HOST="redis://192.168.72.139:6379";
    public static String REDISSION_HOST;

    @Override
    public void afterPropertiesSet() throws Exception {
        REDISSION_HOST = "redis://" + redis_host + ":" + redis_port;
    }
}
