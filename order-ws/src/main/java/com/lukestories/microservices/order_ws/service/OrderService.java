package com.lukestories.microservices.order_ws.service;

import com.lukestories.microservices.order_ws.client.InventoryServiceClient;
import com.lukestories.microservices.order_ws.model.Invoice;
import com.lukestories.microservices.order_ws.model.Order;
import com.lukestories.microservices.order_ws.model.OrderItem;
import com.lukestories.microservices.order_ws.repository.DiscountVoucherRepository;
import com.lukestories.microservices.order_ws.repository.OrderItemRepository;
import com.lukestories.microservices.order_ws.repository.OrderRepository;
import com.lukestories.microservices.order_ws.repository.StatusRepository;
import feign.FeignException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.internal.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

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
    @Autowired
    private DiscountVoucherRepository voucherRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final Long VOUCHER_MAY_SALE_ID = 1l;

    @Transactional(readOnly = true)
    public Order get(Long orderId) {
        Optional<Order> byId = orderRepository.findById(orderId);
        Order byIdWithSubjects = orderRepository.findByOrderIdWithOrderItemsAsStandardJoin(orderId);
        Assert.notNull(byIdWithSubjects);
        byIdWithSubjects.getListOrderItems().forEach(System.out::println);

        Optional<List<Order>> byUserId = orderRepository.findAllByUserId("luke-green");
        Assert.isTrue(byUserId.isPresent());
        System.out.println("[tip] Optional allows to not fetch data from repo until it gets invoked. Check hibernate selects when occur");
//        byUserId.get().forEach(System.out::println);

        Long sumByUserId = orderRepository.getTotalNumberOrdersByUserId("luke-green");
        Assert.notNull(sumByUserId);
        System.out.println("total nbr orders for luke-green: " + sumByUserId);

        sumByUserId = orderRepository.getTotalNumberOrdersByUserId("bob");
        Assert.notNull(sumByUserId);
        System.out.println("total nbr orders for bob: " + sumByUserId);

        Long totalPrice4Order = orderRepository.getTotalPrice4Order(2L);
        Assert.notNull(totalPrice4Order);
        System.out.println("total price for order 2: " + totalPrice4Order);

        return byId.get();
    }

    public Integer getInventory4Product(Long productId) {
        return inventoryServiceClient.getInventory4Product(productId);
    }

    @Transactional(isolation = Isolation.READ_UNCOMMITTED)
    public Order processAndCreateOrder(OrderItem orderItem) throws Exception {

        log.info("create end point");
        try {
            final String status = inventoryServiceClient.status();
            log.info("status inventory: {}", status);

            Boolean isProductInStock;
            isProductInStock = inventoryServiceClient.isProductInStock(orderItem.getProductId());
            if (!isProductInStock) {
                throw noInventory4Product();
            }
            // feign logger traces this info so we dont need to call manually log.info
            inventoryServiceClient.getInventory4Product(orderItem.getProductId());
        } catch (FeignException e) {
            log.error("[go on or throw ex] traced feign exception: {}", e.getMessage());
        } catch (Exception e) {
            log.error("[go on or throw ex] exception: {}", e.getMessage());
        }

        //validate orderItem
        Order saved = createOrder(orderItem);
        entityManager.clear();

        final List<OrderItem> byOrderId = orderItemRepository.findByOrderId(saved.getId());
        log.info("green is good " + byOrderId.size());
        byOrderId.forEach(o -> log.info(o.toString()));

        return saved;
    }

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
        OrderItem orderItem2Fake = detachedEntity.toBuilder().build();
        orderItem2Fake.setId(null);
        orderItem2Fake.setPrice(123.0);
        orderItem2Fake.setOrder(saved);
        orderItem2Fake = orderItemRepository.save(orderItem2Fake);
        saved.setListOrderItems(Set.of(orderItem, orderItem2Fake));


        try {
            inventoryServiceClient.decreaseInventoryAmount4Product(orderItem2Fake.getProductId(), orderItem2Fake.getAmount());
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
