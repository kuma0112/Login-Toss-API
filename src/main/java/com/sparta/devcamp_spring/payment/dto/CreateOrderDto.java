package com.sparta.devcamp_spring.payment.dto;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import com.sparta.devcamp_spring.payment.entity.OrderItem;
import com.sparta.devcamp_spring.payment.entity.ShippingInfo;
import lombok.Getter;
import lombok.Setter;


import java.util.List;

@Getter
@Setter
public class CreateOrderDto {
    private User user;
    private List<OrderItem> orderItems;
    private IssuedCoupon coupon;
    private Double pointAmountToUse;
    private ShippingInfo shippingInfo;
}
