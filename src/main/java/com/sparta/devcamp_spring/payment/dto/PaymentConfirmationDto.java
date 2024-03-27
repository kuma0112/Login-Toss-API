package com.sparta.devcamp_spring.payment.dto;

public class PaymentConfirmationDto {
    private boolean success;
    private String status;

    public PaymentConfirmationDto(boolean success, String status) {
        this.success = success;
        this.status = status;
    }

    // 성공 여부를 반환합니다.
    public boolean isSuccessful() {
        return success;
    }

    // 결제 상태를 반환합니다.
    public String getStatus() {
        return status;
    }

    // 성공 여부를 설정합니다.
    public void setSuccess(boolean success) {
        this.success = success;
    }

    // 결제 상태를 설정합니다.
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentConfirmationDto{" +
                "success=" + success +
                ", status='" + status + '\'' +
                '}';
    }
}

