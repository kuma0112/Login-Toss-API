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

    final double MINIMUM_POINTS_TO_USE = 2000.0; // 최소 사용 포인트 금액

    @Override
    @Transactional
    public void usePoint(Point point, Double amountToUse, String reason) {
        if (amountToUse < MINIMUM_POINTS_TO_USE) {
            throw new IllegalArgumentException("The minimum amount of points to use is " + MINIMUM_POINTS_TO_USE + ".");
        }

        // 포인트 사용 가능 여부 검사
        if (point.getAvailableAmount() < amountToUse) {
            throw new IllegalArgumentException("Insufficient points available.");
        }

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

    public void addPointsToUser(User user, double orderAmount) {
        double pointsEarned = orderAmount * 0.05; // 주문 금액의 5% 포인트 적립
        // 사용자의 포인트를 찾거나, 없으면 새로 생성
        Point userPoints = pointRepository.findByUser(user);
        if (userPoints == null) {
            userPoints = new Point();
            userPoints.setUser(user);
            userPoints.setAvailableAmount(0);
        }
        userPoints.setAvailableAmount(userPoints.getAvailableAmount() + pointsEarned); // 포인트 추가
        pointRepository.save(userPoints);
    }

}
