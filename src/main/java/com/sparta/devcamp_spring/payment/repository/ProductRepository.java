package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
