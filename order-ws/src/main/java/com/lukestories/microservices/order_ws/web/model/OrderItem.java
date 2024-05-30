package com.lukestories.microservices.order_ws.web.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity(name = "ORDER_ITEM")
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class OrderItem {
    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PRODUCT_ID")
    private Long productId;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "AMOUNT")
    private Integer amount;

    @ManyToOne
    @JoinColumn(name = "ORDER_ID", referencedColumnName = "ID")
    @JsonBackReference
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Order order;

    @ManyToMany
    @JoinTable(name = "order_item_product_map",
            joinColumns = @JoinColumn(name = "order_item_id"),
            inverseJoinColumns = @JoinColumn(name = "product_id123"))
    private Set<Product> productList = new HashSet<>();
}
