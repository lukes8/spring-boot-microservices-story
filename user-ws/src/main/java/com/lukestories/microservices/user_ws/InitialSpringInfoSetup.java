package com.lukestories.microservices.user_ws;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component @Slf4j
public class InitialSpringInfoSetup  {

    private ApplicationContext applicationContext;

    @Value("${spring-info-enabled:disabled}")
    private String springInfoEnabled;

    @Autowired
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener
    public void onApplicationEvent(ApplicationReadyEvent event) {
        printBeanNames();
    }

    public void printBeanNames() {
        if (springInfoEnabled.equalsIgnoreCase("enabled")) {
            String[] beanNames = applicationContext.getBeanDefinitionNames();
            for (String beanName : beanNames) {
                log.info("Bean Name: {}", beanName);
            }
        }
    }
}
