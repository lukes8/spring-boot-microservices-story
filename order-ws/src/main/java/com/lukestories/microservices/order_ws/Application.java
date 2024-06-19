package com.lukestories.microservices.order_ws;

import com.lukestories.microservices.order_ws.web.model.DiscountVoucher;
import com.lukestories.microservices.order_ws.web.model.Status;
import com.lukestories.microservices.order_ws.web.repository.DiscountVoucherRepository;
import com.lukestories.microservices.order_ws.web.repository.StatusRepository;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient @Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private DiscountVoucherRepository voucherRepository;
    @Autowired
    private Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        showCustomEnvAndCmdLineArguments();

        Set<Status> lst = Set.of(Status.builder().id(1L).title("open").build(),
                Status.builder().id(2L).title("done").build(),
                Status.builder().id(3L).title("closed").build());
        statusRepository.saveAll(lst);

        var v = DiscountVoucher.builder().id(1l).code("SALE2024").discountValue(100.0).build();
        voucherRepository.saveAll(Arrays.asList(v));
    }

    private void showCustomEnvAndCmdLineArguments() {

        List<String> list = Arrays.asList("port", "spring.port", "spring.datasource.url", "green_arg", "green_arg2");
        for (String key : list) {
            String property = environment.getProperty(key);
            if (property != null && !property.isEmpty()) {
                log.info("environment_properties<{}>: {}", key, property);
            }
        }
    }

    @Bean
    Logger.Level feignLoggerLover() {
        return Logger.Level.FULL;
    }

    //metrics on url http://localhost:${PORT}/actuator/httpexchanges
    @Bean
    public HttpExchangeRepository httpTraceRepository() {
        return new InMemoryHttpExchangeRepository();
    }
}
