package com.sparta.devcamp_spring.payment.controller;

import com.sparta.devcamp_spring.payment.dto.CreateOrderDto;
import com.sparta.devcamp_spring.payment.facade.PaymentFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final PaymentFacade paymentFacade;
    @PostMapping
    public ResponseEntity<?> initOrder(@RequestBody CreateOrderDto dto) {
        try {
            paymentFacade.initOrder(dto);
            return ResponseEntity.ok().body("주문에 성공하였습니다.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("주문에 실패하였습니다: " + e.getMessage());
        }
    }
}
