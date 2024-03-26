package com.sparta.devcamp_spring.payment.repository;

import com.sparta.devcamp_spring.payment.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
