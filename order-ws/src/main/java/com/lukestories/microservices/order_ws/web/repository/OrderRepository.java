package com.lukestories.microservices.order_ws.web.repository;

import com.lukestories.microservices.order_ws.web.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
