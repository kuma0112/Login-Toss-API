package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.ShippingInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingInfoRepository extends JpaRepository<ShippingInfo, Long> {
}
