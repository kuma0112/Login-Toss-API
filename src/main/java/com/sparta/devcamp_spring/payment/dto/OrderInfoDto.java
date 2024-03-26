package com.sparta.devcamp_spring.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderInfoDto {
    private Double totalPrice;
    private Long orderId;
}
