package com.heimdall.gateway.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.util.CollectionUtils;

import java.util.List;

import static com.heimdall.gateway.values.Values.INSTANCE_INFO;

@Slf4j
@Configuration
public class RegistryInstanceConfig {

    @Autowired
    private DiscoveryClient discoveryClient;

    private List<String> registries_tmp;


    @Async
    @Scheduled(fixedRateString = "${eureka.client.registry-fetch-interval-seconds}000")
    public void registryInstance() {
        List<String> registries = discoveryClient.getServices();

        if (!registries.equals(registries_tmp)) {
            registries_tmp = registries;

            if (!CollectionUtils.isEmpty(registries)) {
                for (String registry : registries) {
                    matchNameSpace(registry);
                }
                log.info("Eureka Instances init : Size=[" + INSTANCE_INFO.size() + "}");
            } else {
                log.info("Eureka Instances is Empty : Clear");

                INSTANCE_INFO.clear();
            }
        }
    }

    private void matchNameSpace(String registry) {
        INSTANCE_INFO.put(registry, registry);
    }

}
