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

    @Bean  //指定默认注入类
    @Primary
    @ConditionalOnProperty(prefix = "es", name = {"host", "port"}, matchIfMissing = false)
    public RestHighLevelClient restHighLevelClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(host, port, "http")));
        return client;
    }
}
