package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.common.security.UserDetailsImpl;
import com.sparta.devcamp_spring.payment.dto.CouponEnrollmentDto;
import com.sparta.devcamp_spring.payment.entity.Coupon;
import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import com.sparta.devcamp_spring.payment.repository.CouponRepository;
import com.sparta.devcamp_spring.payment.repository.IssuedCouponRepository;
import com.sparta.devcamp_spring.payment.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssuedCouponServiceImpl implements IssuedCouponService {
    private final IssuedCouponRepository issuedCouponRepository;
    private final CouponRepository couponRepository;

    @Override
    public void useCoupon(IssuedCoupon issuedCoupon) throws Exception {
        int updatedRows = issuedCouponRepository.useCouponIfValid(issuedCoupon.getId());
        if (updatedRows == 0) {
            throw new Exception("The coupon is either already used or not valid.");
        }

    }

    @Override
    public boolean isValidCoupon(IssuedCoupon issuedCoupon) {
        Date now = new Date();
        return issuedCoupon.isValid() && !issuedCoupon.isUsed() && now.after(issuedCoupon.getValidFrom()) && now.before(issuedCoupon.getValidUntil());
    }

    @Override
    public boolean isCouponTypeValid(IssuedCoupon issuedCoupon) {
        return issuedCoupon.getCoupon().getCouponType().equalsIgnoreCase("PERCENT-OFF") || issuedCoupon.getCoupon().getCouponType().equalsIgnoreCase("FIXED-AMOUNT-OFF");
    }

    @Override
    public ResponseEntity<IssuedCoupon> enrollCoupon(UserDetailsImpl userDetails, CouponEnrollmentDto enrollmentDto) {
        Coupon coupon = couponRepository.findById(enrollmentDto.getCouponId()).orElse(null);
        if(coupon == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        // 쿠폰 발행
        IssuedCoupon issuedCoupon = new IssuedCoupon(userDetails.getUser(), coupon);
        issuedCouponRepository.save(issuedCoupon);

        return ResponseEntity.ok(issuedCoupon);
    }

    private Optional<IssuedCoupon> findCouponById(Long couponId) {
        return issuedCouponRepository.findById(couponId);
    }
}
