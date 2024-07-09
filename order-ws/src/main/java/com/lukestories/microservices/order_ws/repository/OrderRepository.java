package com.lukestories.microservices.order_ws.repository;

import com.lukestories.microservices.order_ws.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(name = "Order.findByOrderIdWithOrderItemsAsStandardJoin")
    Order findByOrderIdWithOrderItemsAsStandardJoin(@Param("id") Long id);

    @Query(name = "Order.findAllByUserId")
    Optional<List<Order>> findAllByUserId(@Param("userId") String userId);

    @Query(name = "Order.getTotalNumberOrdersByUserId")
    Long getTotalNumberOrdersByUserId(@Param("userId") String userId);

    @Query(name = "Order.getTotalPrice4Order")
    Long getTotalPrice4Order(@Param("orderId") Long orderId);

    @Query("SELECT DISTINCT o.userId FROM Order o")
    Optional<List<String>> findSummaryOfAllUsersThatRaisedOrder();

    @Query("SELECT o.id, o.title, o.userId, o.price, o.amount, o.createdDateTime, SUM(oi.price * oi.quantity) " +
            "FROM Order o " +
            "JOIN o.listOrderItems oi " +
            "WHERE o.userId = :userId AND o.id = :orderId ")
    List<Object[]> findOrderFieldsByUserIdAndOrderId(@Param("userId") String userId, @Param("orderId") Long orderId);

    @Query("SELECT CONCAT(o.id, '-', o.title, '-', o.userId, '-', o.status, '-', SUM(oi.price * oi.amount)) " +
            "FROM Order o " +
            "JOIN o.listOrderItems oi " +
            "WHERE o.userId = :userId AND o.id = :orderId ")
    String findOrderSummaryByOrderIdAndUserId(@Param("userId") String userId, @Param("orderId") Long orderId);
}
