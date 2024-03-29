package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.dto.PointResponseDto;
import com.sparta.devcamp_spring.payment.entity.Point;
import com.sparta.devcamp_spring.payment.entity.PointLog;
import com.sparta.devcamp_spring.payment.repository.PointLogRepository;
import com.sparta.devcamp_spring.payment.repository.PointRepository;
import com.sparta.devcamp_spring.payment.service.PointService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointServiceImpl implements PointService {
    private final PointRepository pointRepository;
    private final PointLogRepository pointLogRepository;

    @Override
    @Transactional
    public void usePoint(Point point, Double amountToUse, String reason) {
        PointLog log = PointLog.use(point, amountToUse, reason);
        pointLogRepository.save(log);
        point.getLogs().add(log);
        point.use(amountToUse);
        pointRepository.save(point);
    }

    @Override
    public PointResponseDto checkPointAndPointHistory(User user) {
        Point point = pointRepository.findByUserId(user.getId());
        List<PointLog> pointLog = pointLogRepository.findByPoint(point);
        return new PointResponseDto(point.getAvailableAmount(), pointLog);
    }
}
