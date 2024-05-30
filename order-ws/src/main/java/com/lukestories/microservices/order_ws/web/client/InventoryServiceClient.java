package com.lukestories.microservices.order_ws.web.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "inventory-service", url = "localhost:8082/rest/api/inventory")
public interface InventoryServiceClient {

    @GetMapping("/status")
    String status();

    @GetMapping("/{productId}")
    Integer getInventory4Product(@PathVariable Long productId);

    @GetMapping("/isInStock/{productId}")
    Boolean isProductInStock(@PathVariable Long productId);

    @PostMapping("/decreaseAmount4Product/{productId}/{amount}")
    void decreaseInventoryAmount4Product(@PathVariable Long productId, @PathVariable Integer amount);

}
