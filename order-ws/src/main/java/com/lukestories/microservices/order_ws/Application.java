package com.lukestories.microservices.order_ws;

import com.lukestories.microservices.order_ws.web.model.DiscountVoucher;
import com.lukestories.microservices.order_ws.web.model.Status;
import com.lukestories.microservices.order_ws.web.repository.DiscountVoucherRepository;
import com.lukestories.microservices.order_ws.web.repository.StatusRepository;
import feign.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.web.exchanges.HttpExchangeRepository;
import org.springframework.boot.actuate.web.exchanges.InMemoryHttpExchangeRepository;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private DiscountVoucherRepository voucherRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        Set<Status> lst = Set.of(Status.builder().id(1L).title("open").build(),
                Status.builder().id(2L).title("done").build(),
                Status.builder().id(3L).title("closed").build());
        statusRepository.saveAll(lst);

        var v = DiscountVoucher.builder().id(1l).code("SALE2024").discountValue(100.0).build();
        voucherRepository.saveAll(Arrays.asList(v));
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
