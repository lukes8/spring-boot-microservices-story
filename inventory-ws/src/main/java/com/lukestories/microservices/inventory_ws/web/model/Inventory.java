package com.lukestories.microservices.inventory_ws.web.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "INVENTORY")
@Builder
@NoArgsConstructor
@AllArgsConstructor @Data
public class Inventory {
    @Id
    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "AMOUNT")
    private Integer amount;
}
