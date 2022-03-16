package com.wgml.itmall.utils;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;

import javax.jms.Connection;
import javax.jms.JMSException;

/**
 * @author Ye Linfang
 * @date 2021/9/1 13:15
 * @description
 * @since JDK1.8
 */
public class ActiveMQUtil {
    private PooledConnectionFactory pooledConnectionFactory = null;

    public void init(String brokerUrl) {
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        pooledConnectionFactory = new PooledConnectionFactory(activeMQConnectionFactory);
        //设置超时时间
        pooledConnectionFactory.setExpiryTimeout(2000);
        // 设置出现异常的时候，继续重试连接
        pooledConnectionFactory.setReconnectOnException(true);
        // 设置最大连接数
        pooledConnectionFactory.setMaxConnections(5);
    }

    // 获取连接
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = pooledConnectionFactory.createConnection();
        } catch (JMSException e) {
            e.printStackTrace();
        }
        return connection;
    }
}

