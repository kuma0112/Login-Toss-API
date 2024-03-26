package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import com.sparta.devcamp_spring.payment.repository.IssuedCouponRepository;
import com.sparta.devcamp_spring.payment.service.IssuedCouponService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IssuedCouponServiceImpl implements IssuedCouponService {
    private final IssuedCouponRepository issuedCouponRepository;

    @Override
    public void useCoupon(IssuedCoupon issuedCoupon) throws Exception {
        if (!isValidCoupon(issuedCoupon) || !isCouponTypeValid(issuedCoupon)) {
            throw new Exception("Cannot use this coupon due to invalid state or type.");
        }

        issuedCoupon.use();
        issuedCouponRepository.save(issuedCoupon);
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

    private Optional<IssuedCoupon> findCouponById(Long couponId) {
        return issuedCouponRepository.findById(couponId);
    }
}
