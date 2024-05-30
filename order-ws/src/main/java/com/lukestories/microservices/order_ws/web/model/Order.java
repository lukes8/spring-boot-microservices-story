package com.lukestories.microservices.order_ws.web.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity(name = "ORDERS")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "USER_ID")
    private String userId;
    @Column(name = "PRICE")
    private Double price;
    @Column(name = "AMOUNT")
    private Integer amount;
    @Column(name = "CREATEDDATETIME")
    private LocalDateTime createdDateTime;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")
    private Status status;

    //un-directional oneToMany relation, better performance
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<OrderItem> listOrderItems = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "ID")
    private Invoice invoice;

    @ManyToMany
    @JoinTable(name = "order_discount_voucher_map",
            joinColumns = @JoinColumn(name = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "DISCOUNT_VOUCHER_ID"))
    private Set<DiscountVoucher> vouchers = new HashSet<>();
}
