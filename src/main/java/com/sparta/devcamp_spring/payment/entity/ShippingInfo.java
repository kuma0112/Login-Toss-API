package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class ShippingInfo extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    private Order order;

    @Column(length = 1000)
    private String address;

    @Column(length = 50)
    private String status;

    @Column(length = 100, nullable = true)
    private String trackingNumber;

    @Column(length = 50, nullable = true)
    private String shippingCompany;
}
