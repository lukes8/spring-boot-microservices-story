package com.lukestories.microservices.order_ws.web.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = InventoryServiceClient.SERVICE_NAME+ "/rest/api/inventory")
public interface InventoryServiceClient {

    String SERVICE_NAME = "inventory-ws";

    @GetMapping("/status")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "statusFallback")
    String status();
    default String statusFallback(Throwable exception) {
        return "unknown";
    }

    @GetMapping("/{productId}")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "getInventory4ProductFallback")
    Integer getInventory4Product(@PathVariable Long productId);
    default Integer getInventory4ProductFallback(@PathVariable Long productId, Throwable exception) {
        System.out.println("green is good circuit");
        return 0;
    }

    @GetMapping("/isInStock/{productId}")
    @Retry(name = SERVICE_NAME)
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "isProductInStockFallback")
    Boolean isProductInStock(@PathVariable Long productId);
    default Boolean isProductInStockFallback(@PathVariable Long productId, Throwable exception) {
        return false;
    }

    @PostMapping("/decreaseAmount4Product/{productId}/{amount}")
    @CircuitBreaker(name = SERVICE_NAME, fallbackMethod = "decreaseInventoryAmount4ProductFallback")
    void decreaseInventoryAmount4Product(@PathVariable Long productId, @PathVariable Integer amount);
    default void decreaseInventoryAmount4ProductFallback(@PathVariable Long productId, @PathVariable Integer amount, Throwable throwable) {

    }
}
