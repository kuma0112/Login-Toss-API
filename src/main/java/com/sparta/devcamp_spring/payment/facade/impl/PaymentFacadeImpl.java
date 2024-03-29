package com.sparta.devcamp_spring.payment.facade.impl;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.dto.CreateOrderDto;
import com.sparta.devcamp_spring.payment.dto.OrderInfoDto;
import com.sparta.devcamp_spring.payment.dto.OrderResponseDto;
import com.sparta.devcamp_spring.payment.entity.Order;
import com.sparta.devcamp_spring.payment.entity.Product;
import com.sparta.devcamp_spring.payment.facade.PaymentFacade;
import com.sparta.devcamp_spring.payment.service.IssuedCouponService;
import com.sparta.devcamp_spring.payment.service.OrderService;
import com.sparta.devcamp_spring.payment.service.PointService;
import com.sparta.devcamp_spring.payment.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class PaymentFacadeImpl implements PaymentFacade {
    private final OrderService orderService;
    private final PointService pointService;
    private final IssuedCouponService issuedCouponService;
    private final ProductService productService;

    @Override
    @Transactional
    public OrderInfoDto getOrderInfo(Long orderId) throws Exception {
        OrderInfoDto orderInfo = new OrderInfoDto();
        Order orderById = orderService.getOrderById(orderId);
        orderInfo.setOrderId(orderId);
        orderInfo.setTotalPrice(orderById.getAmount());
        return orderInfo;
    }

    @Override
    @Transactional
    public OrderResponseDto initOrder(CreateOrderDto createOrderDto) throws Exception {
        Order order = orderService.createOrder(
                createOrderDto.getUser(),
                createOrderDto.getOrderItems(),
                createOrderDto.getShippingInfo());
        // 쿠폰 적용 후 주문 예상 금액
        double minimumOrderAmountAfterCoupons = orderService.calculateMinimumOrderAmountAfterCoupons(order);
        // 쿠폰 적용
        orderService.applyCouponToOrder(order.getId(), createOrderDto.getCoupon());
        // 포인트 적용
        double pointAmountToUse = createOrderDto.getPointAmountToUse(); // 사용자가 원하는 포인트 사용 금액
        double finalAmountAfterPoints = minimumOrderAmountAfterCoupons - pointAmountToUse;
        finalAmountAfterPoints = finalAmountAfterPoints > 0 ? finalAmountAfterPoints : 0; // 최종 금액이 0보다 작아지지 않도록 처리

        orderService.applyPointToOrder(order.getId(), pointAmountToUse);
        return new OrderResponseDto(order.getId(), finalAmountAfterPoints);
    }

    @Override
    @Transactional
    public Long prepareOrder(Long orderId) throws Exception {
        Map<Product, Integer> productIntegerMap = orderService.generateEntry(orderId);
        productService.decreaseStockQuantity(productIntegerMap);
        orderService.standbyOrder(orderId);
        return orderId;
    }

    @Override
    public Long completeOrder(Long orderId, User user) throws Exception {
        Order orderById = orderService.getOrderById(orderId);
        if (!orderById.getStatus().equalsIgnoreCase("STAND_BY")) {
            throw new Exception("Order State is Illegal For Payment");
        }

        issuedCouponService.useCoupon(orderById.getUsedIssuedCoupon());
        String reason = "결제건 사용 : " + orderById.getOrderNumber();
        pointService.usePoint(user.getPoint(), orderById.getPointAmountUsed(), reason);
        orderService.completeOrder(orderById.getId());

        // 주문 금액의 0.5%를 포인트로 적립
        double pointsToEarn = orderById.getAmount() * 0.005;
        String earnReason = "주문 완료 적립 : " + orderById.getOrderNumber();
        // 포인트 적립 로직 호출 (pointService에 적립 메서드 추가가 필요)
        pointService.addPointsToUser(user, pointsToEarn);

        return orderId;
    }

    @Override
    public Long undoOrder(Long orderId) throws Exception {
        Map<Product, Integer> productIntegerMap = orderService.generateEntry(orderId);
        productService.increaseStockQuantity(productIntegerMap);
        orderService.undoOrder(orderId);
        return orderId;
    }
}
