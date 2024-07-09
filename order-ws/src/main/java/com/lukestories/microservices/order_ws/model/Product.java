package com.lukestories.microservices.order_ws.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@Entity(name = "PRODUCT") @Data
public class Product {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "PRICE")
    private Double price;

    @ManyToMany(mappedBy = "productList")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private List<OrderItem> orderItemList;
}
