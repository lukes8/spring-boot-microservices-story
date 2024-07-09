package com.lukestories.microservices.order_ws;

import com.lukestories.microservices.order_ws.model.DiscountVoucher;
import com.lukestories.microservices.order_ws.model.Order;
import com.lukestories.microservices.order_ws.model.OrderItem;
import com.lukestories.microservices.order_ws.model.Status;
import com.lukestories.microservices.order_ws.repository.DiscountVoucherRepository;
import com.lukestories.microservices.order_ws.repository.OrderItemRepository;
import com.lukestories.microservices.order_ws.repository.OrderRepository;
import com.lukestories.microservices.order_ws.repository.StatusRepository;
import feign.Logger;
import jakarta.transaction.Transactional;
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

import java.util.*;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
@Slf4j
public class Application implements ApplicationListener<ContextRefreshedEvent> {

    private static final long ORDER_SIZE = 4;
    private static final long ORDER_ITEM_SIZE = 10;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private DiscountVoucherRepository voucherRepository;
    @Autowired
    private Environment environment;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {

        showCustomEnvAndCmdLineArguments();

        Set<Status> lst = Set.of(Status.builder().id(1L).title("open").build(),
                Status.builder().id(2L).title("done").build(),
                Status.builder().id(3L).title("closed").build());
        statusRepository.saveAll(lst);

        Set<DiscountVoucher> set = new HashSet<>();
        set.add(DiscountVoucher.builder().id(1L).code("SALE2024").discountValue(100.0).build());
        set.add(DiscountVoucher.builder().id(2L).code("GARDEN2024").discountValue(3.0).build());
        set.add(DiscountVoucher.builder().id(3L).code("BLACK_FRIDAY").discountValue(200.0).build());
        voucherRepository.saveAll(set);
//

        var orders = new HashSet<Order>();
        long i;
        for (i = 0; i < ORDER_SIZE; i++) {
            var o = Order.builder().id(i).amount(2 + ((int) i)).price(12.2).title("Banana" + i).userId(
                    Arrays.asList("luke-green", "bob", "polly").get(
                            new Random().nextInt(0, 2)
                    )).build();
            o.setVouchers(set.stream().filter(f -> f.getId() == (new Random().nextInt(0, set.size()))).collect(Collectors.toSet()));
            o.setStatus(new ArrayList<>(lst).get(1));
            orders.add(o);
        }
        orderRepository.saveAll(orders);

        System.out.println("List of users in h2: ");
        orders.stream().map(Order::getUserId).forEach(System.out::println);

        Set<OrderItem> items = new HashSet<>();
        Order next = orders.stream().filter(f -> f.getId() == 2).findFirst().get();
        for (i = 0; i < ORDER_ITEM_SIZE; i++) {
            var oi = OrderItem.builder().id(i).productId(1L).amount(((int) i)).price(121.2).order(next).build();
            items.add(oi);
        }
        orderItemRepository.saveAll(items);

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
