package com.lukestories.microservices.order_ws.repository;

import com.lukestories.microservices.order_ws.model.DiscountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountVoucherRepository extends JpaRepository<DiscountVoucher, Long> {
}
