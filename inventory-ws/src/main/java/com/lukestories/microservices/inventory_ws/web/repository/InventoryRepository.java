package com.lukestories.microservices.inventory_ws.web.repository;

import com.lukestories.microservices.inventory_ws.web.model.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
}
