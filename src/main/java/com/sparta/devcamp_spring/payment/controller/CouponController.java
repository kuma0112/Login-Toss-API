package com.sparta.devcamp_spring.payment.controller;

import com.sparta.devcamp_spring.common.security.UserDetailsImpl;
import com.sparta.devcamp_spring.payment.dto.CouponEnrollmentDto;
import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import com.sparta.devcamp_spring.payment.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/points")
public class CouponController {
    private final IssuedCouponService issuedCouponService;

    public ResponseEntity<IssuedCoupon> enrollCoupon(@AuthenticationPrincipal UserDetailsImpl userDetails, @RequestBody CouponEnrollmentDto enrollmentDto){
        return issuedCouponService.enrollCoupon(userDetails, enrollmentDto);
    }
}
