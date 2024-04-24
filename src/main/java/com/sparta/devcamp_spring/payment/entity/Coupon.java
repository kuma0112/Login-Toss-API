package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.List;

@Entity
@Getter
    public class Coupon extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;
        @Column(length = 50)
        private String couponType;
        @Column
        private Double amount; // 할인율 또는 정액 금액

        @OneToMany(mappedBy = "coupon")
        private List<IssuedCoupon> issuedCoupons;
    }
