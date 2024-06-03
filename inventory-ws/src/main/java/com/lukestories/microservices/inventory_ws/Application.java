package com.lukestories.microservices.inventory_ws;

import com.lukestories.microservices.inventory_ws.web.repository.InventoryRepository;
import com.lukestories.microservices.inventory_ws.web.util.InventoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Arrays;

@SpringBootApplication
@EnableDiscoveryClient
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private InventoryRepository inventoryRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        inventoryRepository.saveAll(InventoryUtil.getList());
    }
}
