package com.sparta.devcamp_spring.payment.service;

import com.sparta.devcamp_spring.common.security.UserDetailsImpl;
import com.sparta.devcamp_spring.payment.dto.CouponEnrollmentDto;
import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import org.springframework.http.ResponseEntity;

public interface IssuedCouponService {
    /**
     * 발급된 쿠폰을 사용처리한다
     * @param issuedCoupon coupon entity
     */
    void useCoupon(IssuedCoupon issuedCoupon) throws Exception;

    /**
     * 발급된 쿠폰이 유효한지 확인한다
     * @param issuedCoupon coupon entity
     * @return 쿠푠 유효성 여부
     */
    boolean isValidCoupon(IssuedCoupon issuedCoupon);

    /**
     * 쿠폰의 타입을 확인한다
     * @return 쿠폰 타입 적합한지 확인 여부
     */
    boolean isCouponTypeValid(IssuedCoupon issuedCoupon);

    ResponseEntity<IssuedCoupon> enrollCoupon(UserDetailsImpl userDetails, CouponEnrollmentDto enrollmentDto);
}
