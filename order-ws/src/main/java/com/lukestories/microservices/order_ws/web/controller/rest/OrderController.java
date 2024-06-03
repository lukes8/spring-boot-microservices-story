package com.lukestories.microservices.order_ws.web.controller.rest;

import com.lukestories.microservices.order_ws.web.model.Order;
import com.lukestories.microservices.order_ws.web.model.OrderItem;
import com.lukestories.microservices.order_ws.web.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/api/orders")
@Slf4j
public class OrderController {

    @Autowired private OrderService orderService;

    @GetMapping("/status/check")
    public String status() {
        return "green world";
    }

    @GetMapping(value = "/{orderId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Order get(@PathVariable Long orderId) {
        log.info("order {}", orderId);
        Order order = orderService.get(orderId);
        return order;
    }

    @GetMapping(value = "/get-inventory/{productId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public Integer getInventory4Product(@PathVariable Long productId) {
        return orderService.getInventory4Product(productId);
    }

//    @PostMapping
//    public String create(@RequestParam(defaultValue = "-1") Long orderId,
//                         @RequestParam String productName,
//                         @RequestParam(required = false, defaultValue = "1") Long count) {
//        return "created " + String.format("orderId: %s, productName: %s, count: %s", orderId, productName, count);
//    }

    @PostMapping
    public ResponseEntity<Order> create(@RequestBody OrderItem orderItem) {

        Order created = null;
        try {
            created = orderService.processAndCreateOrder(orderItem);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
        return ResponseEntity.ok(created);
    }

}
