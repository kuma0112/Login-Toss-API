package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
