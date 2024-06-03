package com.lukestories.microservices.order_ws.web.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-ws/rest/api/inventory")
public interface InventoryServiceClient {

    @GetMapping("/status")
    @CircuitBreaker(name = "status", fallbackMethod = "statusFallback")
    String status();
    default String statusFallback(Throwable exception) {
        return "unknown";
    }

    @GetMapping("/{productId}")
    @CircuitBreaker(name = "getInventory4Product", fallbackMethod = "getInventory4ProductFallback")
    Integer getInventory4Product(@PathVariable Long productId);
    default Integer getInventory4ProductFallback(@PathVariable Long productId, Throwable exception) {
        System.out.println("green is good circuit");
        return 0;
    }

    @GetMapping("/isInStock/{productId}")
    @CircuitBreaker(name = "isProductInStock", fallbackMethod = "isProductInStockFallback")
    Boolean isProductInStock(@PathVariable Long productId);
    default Boolean isProductInStockFallback(@PathVariable Long productId, Throwable exception) {
        return false;
    }

    @PostMapping("/decreaseAmount4Product/{productId}/{amount}")
    @CircuitBreaker(name = "decreaseInventoryAmount4Product", fallbackMethod = "decreaseInventoryAmount4ProductFallback")
    void decreaseInventoryAmount4Product(@PathVariable Long productId, @PathVariable Integer amount);
    default void decreaseInventoryAmount4ProductFallback(@PathVariable Long productId, @PathVariable Integer amount, Throwable throwable) {

    }
}
