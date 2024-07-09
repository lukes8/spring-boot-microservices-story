package com.lukestories.microservices.order_ws.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity(name = "ORDER_CATEGORY")
public class Category {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "ACTIVE")
    private Boolean active;
}
