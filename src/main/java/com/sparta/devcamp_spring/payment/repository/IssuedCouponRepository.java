package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.entity.Coupon;
import com.sparta.devcamp_spring.payment.entity.IssuedCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssuedCouponRepository extends JpaRepository<IssuedCoupon, Long> {
    @Modifying
    @Query("UPDATE IssuedCoupon ic SET ic.isUsed = true, ic.isValid = false, ic.usedAt = CURRENT_TIMESTAMP WHERE ic.id = :couponId AND ic.isUsed = false AND ic.isValid = true")
    int useCouponIfValid(Long couponId);

    List<IssuedCoupon> findAllByUser(User user);

}
