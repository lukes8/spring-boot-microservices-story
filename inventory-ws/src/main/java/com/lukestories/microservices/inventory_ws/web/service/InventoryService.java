package com.lukestories.microservices.inventory_ws.web.service;

import com.lukestories.microservices.inventory_ws.web.model.Inventory;
import com.lukestories.microservices.inventory_ws.web.repository.InventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {
    @Autowired private InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Integer getInventory4Product(Long productId) throws Exception {
        final Inventory byId = inventoryRepository.findById(productId).orElseThrow(InventoryService::notFound);
        return byId.getAmount();
    }

    @Transactional(readOnly = true)
    public Inventory getById(Long productId) throws Exception {
        final Inventory byId = inventoryRepository.findById(productId).orElseThrow(InventoryService::notFound);
        return byId;
    }

    @Transactional(readOnly = true)
    public Boolean isInStock(Long productId) throws Exception {
        final Inventory byId = inventoryRepository.findById(productId).orElseThrow(InventoryService::notFound);
        return byId.getAmount() > 0;
    }

    @Transactional(isolation = Isolation.DEFAULT)
    public void decreaseInventory(Long productId, Integer amount) throws Exception {
        final Inventory byId = getById(productId);
        if (byId.getAmount() < amount) {
            throw insufficientInventory();
        }
        byId.setAmount(byId.getAmount() - amount);
        inventoryRepository.save(byId);
    }

    public static Exception notFound() {
        return new Exception("NOT_FOUND");
    }
    public static Exception insufficientInventory() {
        return new Exception("INSUFFICIENT_INVENTORY");
    }
}
