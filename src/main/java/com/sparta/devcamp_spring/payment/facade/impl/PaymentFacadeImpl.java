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

        double minimumOrderAmountAfterCoupons = orderService.calculateMinimumOrderAmountAfterCoupons(order);
        orderService.applyCouponToOrder(order.getId(), createOrderDto.getCoupon());
        orderService.applyPointToOrder(order.getId(), createOrderDto.getPointAmountToUse());
        return new OrderResponseDto(order.getId(), minimumOrderAmountAfterCoupons);
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
        // TODO: useEntity 업데이트시 주석 제거
//        pointService.usePoint(user.getPoint(), orderById.getPointAmountUsed(), reason);
        orderService.completeOrder(orderById.getId());
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
