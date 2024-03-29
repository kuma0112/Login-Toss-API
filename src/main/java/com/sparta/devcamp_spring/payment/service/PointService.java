package com.sparta.devcamp_spring.payment.service;


import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.dto.PointResponseDto;
import com.sparta.devcamp_spring.payment.entity.Point;

public interface PointService {
    /**
     * 포인트를 사용처리한다
     * @param point point entity
     * @param amountToUse 사용할 금액
     * @param reason 사용 사유
     */
    void usePoint(Point point, Double amountToUse, String reason);

    PointResponseDto checkPointAndPointHistory(User user);
}
