package com.imooc.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @description:
 * @author: lixiahan
 * @create: 2019/07/09 22:40
 */
@Configuration
public class ElasticSearchConfig {

    @Bean
    public TransportClient esClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "xunwu")
                .put("client.transport.sniff", true)
                .build();

        InetSocketTransportAddress master =
                new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300);

        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(master);

        return client;
    }
}
