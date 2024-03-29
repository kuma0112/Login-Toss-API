package com.sparta.devcamp_spring.payment.repository;


import com.sparta.devcamp_spring.payment.entity.Point;
import com.sparta.devcamp_spring.payment.entity.PointLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PointLogRepository extends JpaRepository<PointLog, Long> {
    List<PointLog> findByPoint(Point point);
}
