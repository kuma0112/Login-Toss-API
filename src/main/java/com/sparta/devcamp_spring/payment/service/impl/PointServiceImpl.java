package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.payment.entity.Point;
import com.sparta.devcamp_spring.payment.entity.PointLog;
import com.sparta.devcamp_spring.payment.repository.PointLogRepository;
import com.sparta.devcamp_spring.payment.repository.PointRepository;
import com.sparta.devcamp_spring.payment.service.PointService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    @Override
    @Transactional
    public void usePoint(Point point, int amountToUse, String reason) {
        PointLog log = PointLog.use(point, amountToUse, reason);
        pointLogRepository.save(log);
        point.getLogs().add(log);
        point.use(amountToUse);
        pointRepository.save(point);
    }
}
