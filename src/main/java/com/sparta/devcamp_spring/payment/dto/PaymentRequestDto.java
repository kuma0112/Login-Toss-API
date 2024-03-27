package com.sparta.devcamp_spring.payment.dto;

public class PaymentRequestDto {
    private String paymentKey;
    private String orderId;
    private String amount;

    // 생성자
    public PaymentRequestDto(String paymentKey, String orderId, String amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }

    // paymentKey에 대한 게터 메서드
    public String getPaymentKey() {
        return paymentKey;
    }

    // orderId에 대한 게터 메서드
    public String getOrderId() {
        return orderId;
    }

    // amount에 대한 게터 메서드
    public String getAmount() {
        return amount;
    }

    // toString 메서드 오버라이드 (선택적)
    @Override
    public String toString() {
        return "PaymentRequestDto{" +
                "paymentKey='" + paymentKey + '\'' +
                ", orderId='" + orderId + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
