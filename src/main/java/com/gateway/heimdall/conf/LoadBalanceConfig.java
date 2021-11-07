package com.gateway.heimdall.conf;

import lombok.Data;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Data
@Configuration
@ConfigurationProperties("gateway.lb.conf")
public class LoadBalanceConfig {

    private int connTimeout;

    private int connReqTimeout;

    private int readTimeout;

    private int maxConnTotal;

    private int maxConnPerRoute;


    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate(new BufferingClientHttpRequestFactory(setHttpComponentsClientHttpRequestFactory()));
    }

    private HttpComponentsClientHttpRequestFactory setHttpComponentsClientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setHttpClient(setHttpClient());
        factory.setConnectTimeout(connTimeout);
        factory.setConnectionRequestTimeout(connReqTimeout);
        factory.setReadTimeout(readTimeout);
        return factory;
    }

    private HttpClient setHttpClient() {
        return HttpClientBuilder.create().setMaxConnTotal(maxConnTotal).setMaxConnPerRoute(maxConnPerRoute).build();
    }

}
