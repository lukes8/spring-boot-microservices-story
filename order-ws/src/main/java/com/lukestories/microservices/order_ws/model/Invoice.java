package com.lukestories.microservices.order_ws.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Builder @NoArgsConstructor @AllArgsConstructor @Data
public class Invoice {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;

}
