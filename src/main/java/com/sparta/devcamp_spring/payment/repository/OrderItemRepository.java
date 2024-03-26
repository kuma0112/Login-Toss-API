package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
