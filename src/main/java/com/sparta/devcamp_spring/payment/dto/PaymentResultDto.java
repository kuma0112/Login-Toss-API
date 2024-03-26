package com.sparta.devcamp_spring.payment.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResultDto {
    private boolean isPaymentSuccess;
    private String message;
}
