package com.lukestories.microservices.order_ws.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.Set;

@Entity(name = "DISCOUNT_VOUCHER") @Builder @NoArgsConstructor @AllArgsConstructor @Data
public class DiscountVoucher {
    @Id
    @Column(name = "ID")
    private Long id;
    @Column(name = "CODE")
    private String code;
    @Column(name = "DISCOUNT_VALUE")
    private Double discountValue;

    @ManyToMany(mappedBy = "vouchers")
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Set<Order> orders;

}
