package com.lukestories.microservices.inventory_ws.web.controller.rest;

import com.lukestories.microservices.inventory_ws.web.service.InventoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/inventory")
@Slf4j
public class InventoryController {

    @Autowired private InventoryService inventoryService;
    @Autowired private Environment environment;

    @GetMapping("/status/check")
    public String status(HttpServletRequest request) {
        String portNbr = environment.getProperty("local.server.port");
        return "[check] running on port " + (portNbr != null ? portNbr : "?");
    }

    @GetMapping("/{productId}")
    public Integer getInventory4Product(@PathVariable Long productId) throws Exception {
        return inventoryService.getInventory4Product(productId);
    }
    @GetMapping("/isInStock/{productId}")
    public Boolean isProductInStock(@PathVariable Long productId) throws Exception {
        log.debug("is product in stock? Product id {}", productId);
        return inventoryService.isInStock(productId);
    }
    @PostMapping("/decreaseAmount4Product/{productId}/{amount}")
    public void decreaseInventoryAmount4Product(@PathVariable Long productId, @PathVariable Integer amount) throws Exception {
        inventoryService.decreaseInventory(productId, amount);
    }
}
