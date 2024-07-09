package com.lukestories.microservices.order_ws.repository;

import com.lukestories.microservices.order_ws.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StatusRepository extends JpaRepository<Status, Long> {
}
