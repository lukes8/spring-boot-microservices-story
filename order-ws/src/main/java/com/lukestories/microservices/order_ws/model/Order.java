package com.lukestories.microservices.order_ws.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "ORDERS")
@NamedQueries({

        @NamedQuery(
                name = "Order.findAllByUserId",
                query = "SELECT o FROM Order o WHERE o.userId = :userId"
        ),
        @NamedQuery(
                name = "Order.getTotalNumberOrdersByUserId",
                query = "SELECT COUNT(o) FROM Order o WHERE o.userId = :userId"
        ),
        @NamedQuery(
                name = "Order.getTotalPrice4Order",
                query = "SELECT SUM(oi.price) FROM OrderItem oi WHERE oi.order.id = :orderId"
        ),
        @NamedQuery(
                name = "Order.findByOrderIdWithOrderItemsAsStandardJoin",
                query = "SELECT o FROM Order o JOIN FETCH o.listOrderItems WHERE o.id = :id"
        ),
        @NamedQuery(
                name = "Order.findAllByVoucherTypeWithOrderItemsAsCustomJoin",
                query = "SELECT o FROM Order o JOIN OrderItem oi ON o.id = oi.order_id"
        )
})
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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "STATUS_ID", referencedColumnName = "ID")
    private Status status;

    //un-directional oneToMany relation, better performance
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private Set<OrderItem> listOrderItems = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "INVOICE_ID", referencedColumnName = "ID")
    private Invoice invoice;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "order_discount_voucher_map",
            joinColumns = @JoinColumn(name = "ORDER_ID"), inverseJoinColumns = @JoinColumn(name = "DISCOUNT_VOUCHER_ID"))
    private Set<DiscountVoucher> vouchers = new HashSet<>();
}
