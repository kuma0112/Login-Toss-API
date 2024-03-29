package com.sparta.devcamp_spring.payment.controller;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.common.security.UserDetailsImpl;
import com.sparta.devcamp_spring.payment.dto.CreateOrderDto;
import com.sparta.devcamp_spring.payment.dto.PointResponseDto;
import com.sparta.devcamp_spring.payment.facade.PaymentFacade;
import com.sparta.devcamp_spring.payment.service.PointService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class PointController {
    private final PointService pointService;

    @GetMapping
    public ResponseEntity<PointResponseDto> checkPointAndPointHistory(@AuthenticationPrincipal UserDetailsImpl userDetails){
        PointResponseDto pointResponseDto = pointService.checkPointAndPointHistory(userDetails.getUser());
        return ResponseEntity.ok().body(pointResponseDto);
    }
}
