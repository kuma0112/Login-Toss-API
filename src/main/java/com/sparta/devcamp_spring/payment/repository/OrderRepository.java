package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
