package com.tc.hoodwatch;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SuppressWarnings("ALL")
@Configuration
public class Beans {
    public static final String ES_HOST = "hack.cmlteam.com";
    private final RestClient restClient;
    private final RestHighLevelClient highLevelClient;

    public Beans() {
        restClient = RestClient.builder(new HttpHost(ES_HOST, 80)).build();

        highLevelClient = new RestHighLevelClient(restClient);
    }

    @Bean
    public RestClient getRestClient() {
        return restClient;
    }

    @Bean
    public RestHighLevelClient getHighLevelClient() {
        return highLevelClient;
    }
}
