package com.lukestories.microservices.order_ws.web.service;

import com.lukestories.microservices.order_ws.web.client.InventoryServiceClient;
import com.lukestories.microservices.order_ws.web.model.Invoice;
import com.lukestories.microservices.order_ws.web.model.Order;
import com.lukestories.microservices.order_ws.web.model.OrderItem;
import com.lukestories.microservices.order_ws.web.repository.DiscountVoucherRepository;
import com.lukestories.microservices.order_ws.web.repository.OrderItemRepository;
import com.lukestories.microservices.order_ws.web.repository.OrderRepository;
import com.lukestories.microservices.order_ws.web.repository.StatusRepository;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Slf4j
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private StatusRepository statusRepository;
    @Autowired
    private InventoryServiceClient inventoryServiceClient;
    @Autowired private DiscountVoucherRepository voucherRepository;

    private final Long VOUCHER_MAY_SALE_ID = 1l;

    public Order get(Long orderId) {
        Optional<Order> byId = orderRepository.findById(orderId);
        return byId.get();
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Order processAndCreateOrder(OrderItem orderItem) throws Exception {

        log.info("create end point");
        final String status = inventoryServiceClient.status();
        log.info("status inventory: {}", status);

        Boolean isProductInStock;
        try {
            isProductInStock = inventoryServiceClient.isProductInStock(orderItem.getProductId());
            if (!isProductInStock) {
                throw noInventory4Product();
            }
            // feign logger traces this info so we dont need to call manually log.info
            inventoryServiceClient.getInventory4Product(orderItem.getProductId());
        } catch (FeignException e) {
            log.error("Traced feign exception: " + e.getMessage());
            throw microserviceCommunicationFailure();
        }

        //validate orderItem
        Order saved = createOrder(orderItem);
        entityManager.clear();

        final List<OrderItem> byOrderId = orderItemRepository.findByOrderId(saved.getId());
        log.info("green is good " + byOrderId.size());
        byOrderId.forEach(o -> log.info(o.toString()));

        return saved;
    }

    @PersistenceContext private EntityManager entityManager;

    private Order createOrder(OrderItem orderItem) throws Exception {

        Long orderId = Math.abs(UUID.randomUUID().getMostSignificantBits());
        Invoice invoice = Invoice.builder().id(Math.abs(UUID.randomUUID().getLeastSignificantBits())).title("invoice-sample").build();
        var order = Order.builder().id(orderId).status(statusRepository.findById(1L).get()).invoice(invoice).build();
        var voucher = voucherRepository.findById(VOUCHER_MAY_SALE_ID).get();
        order.setVouchers(Set.of(voucher));
        Order saved = orderRepository.save(order);
        orderItem.setOrder(saved);
        orderItem = orderItemRepository.save(orderItem);
//        final OrderItem orderItem3 = OrderItem.builder().id(null).amount(1).build();
        entityManager.detach(orderItem);
        final OrderItem detachedEntity = orderItemRepository.findById(orderItem.getId()).orElseThrow(() -> new Exception("NOT_FOUND"));
        OrderItem orderItem2 = detachedEntity.toBuilder().build();
        orderItem2.setId(null);
        orderItem2.setPrice(123.0);
        orderItem2.setOrder(saved);
        orderItem2 = orderItemRepository.save(orderItem2);
        saved.setListOrderItems(Set.of(orderItem, orderItem2));


        try {
            inventoryServiceClient.decreaseInventoryAmount4Product(orderItem2.getProductId(), orderItem2.getAmount());
        } catch (FeignException e) {
            throw microserviceFailure4insufficientInventory();
        }

        return saved;
    }

    public static Exception noInventory4Product() {
        return new Exception("NO_INVENTORY_FOR_PRODUCT");
    }
    public static Exception microserviceCommunicationFailure() {
        return new Exception("MICROSERVICE_COMMUNICATION_FAILURE");
    }
    public static Exception microserviceFailure4insufficientInventory() {
        return new Exception("INSUFFICIENT_INVENTORY");
    }
}
