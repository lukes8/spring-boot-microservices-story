package com.lukestories.microservices.order_ws.web.repository;

import com.lukestories.microservices.order_ws.web.model.DiscountVoucher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiscountVoucherRepository extends JpaRepository<DiscountVoucher, Long> {
}
