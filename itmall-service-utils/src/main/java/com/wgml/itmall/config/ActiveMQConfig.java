package com.wgml.itmall.config;

import com.wgml.itmall.utils.ActiveMQUtil;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;

import javax.jms.Session;

/**
 * @author Ye Linfang
 * @date 2021/9/1 13:19
 * @description
 * @since JDK1.8
 */
@Configuration
//@ConditionalOnBean({ActiveMqSupport.class})
@ConditionalOnProperty(
        prefix = "spring.activemq",
        name = {"broker-url"},
        matchIfMissing = false
)
public class ActiveMQConfig {

    @Value("${spring.activemq.broker-url:disabled}")
    String brokerURL;

    @Value("${activemq.listener.enable:disabled}")
    String listenerEnable;

    // 获取activeMQUtil

    @Bean
    public ActiveMQUtil getActiveMQUtil() {
        if ("disabled".equals(brokerURL)) {
            return null;
        }
        ActiveMQUtil activeMQUtil = new ActiveMQUtil();
        activeMQUtil.init(brokerURL);
        return activeMQUtil;
    }

    /**
     * @return: org.springframework.jms.config.DefaultJmsListenerContainerFactory
     * @author Ye Linfang
     * @date 2021/9/1 19:27
     * @description 配置消息监听器工厂
     */
    @ConditionalOnProperty(
            prefix = "activemq.listener",
            name = {"enable"},
            matchIfMissing = false
    )
    @Bean(name = "jmsQueueListener")
    public DefaultJmsListenerContainerFactory jmsQueueListenerContainerFactory(ActiveMQConnectionFactory activeMQConnectionFactory) {

        if ("disabled".equals(listenerEnable)) {
            return null;
        }
        DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(activeMQConnectionFactory);
        // 设置事务
        factory.setSessionTransacted(false);
        // 手动签收
        factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
        // 设置并发数
        factory.setConcurrency("5");
        // 重连间隔时间
        factory.setRecoveryInterval(5000L);
        return factory;
    }

    // 接收消息

    @Bean
    public ActiveMQConnectionFactory activeMQConnectionFactory() {
        if ("disabled".equals(brokerURL)) {
            return null;
        }
        ActiveMQConnectionFactory activeMQConnectionFactory =
                new ActiveMQConnectionFactory(brokerURL);
        return activeMQConnectionFactory;
    }
}

