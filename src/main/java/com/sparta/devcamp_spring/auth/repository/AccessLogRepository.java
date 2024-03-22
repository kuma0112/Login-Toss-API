package com.sparta.devcamp_spring.auth.repository;

import com.sparta.devcamp_spring.auth.entity.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
}
