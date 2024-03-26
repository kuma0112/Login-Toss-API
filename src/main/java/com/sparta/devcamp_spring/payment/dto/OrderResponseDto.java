package com.sparta.devcamp_spring.payment.dto;

import com.sparta.devcamp_spring.auth.entity.User;

public class OrderResponseDto {
    private Long id;
    private double minimumAmount;

    public OrderResponseDto(Long id, double minimumOrderAmountAfterCoupons) {
        this.id = id;
        this.minimumAmount = minimumOrderAmountAfterCoupons;
    }
}
