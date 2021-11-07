package com.gateway.heimdall.run;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableDiscoveryClient
@EnableRetry
@EnableScheduling
@EnableAsync
@SpringBootApplication(scanBasePackages = "com.gateway.heimdall")
public class Run {


    public static void main(String[] args) {
        SpringApplication.run(Run.class, args);
    }

}
