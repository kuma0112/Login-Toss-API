package com.sparta.devcamp_spring.payment.service.impl;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.entity.*;
import com.sparta.devcamp_spring.payment.repository.OrderItemRepository;
import com.sparta.devcamp_spring.payment.repository.OrderRepository;
import com.sparta.devcamp_spring.payment.service.IssuedCouponService;
import com.sparta.devcamp_spring.payment.service.OrderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final IssuedCouponService issuedCouponService;

    @Override
    public Order createOrder(User user, List<OrderItem> orderItems, ShippingInfo shippingInfo) {
        Order order = new Order(user, orderItems, shippingInfo);
        return orderRepository.save(order);
    }

    @Override
    public Order getOrderById(Long orderId) throws Exception {
       return orderRepository.findById(orderId).orElseThrow(Exception::new);
    }

    @Override
    public Double getOrderCheckoutPrice(Long orderId) throws Exception {
        Order orderById = getOrderById(orderId);
        return orderById.getCheckoutPrice();
    }

    @Override
    @Transactional
    public void applyPointToOrder(Long orderId, Double point) throws Exception {
        Order orderById = getOrderById(orderId);
        orderById.applyPointToOrder(point);
    }

    @Override
    @Transactional
    public void applyCouponToOrder(Long orderId, IssuedCoupon issuedCoupon) throws Exception {
        if (!issuedCouponService.isValidCoupon(issuedCoupon)){
            throw new Exception("This coupon is not valid to use.");
        }

        if (!issuedCouponService.isCouponTypeValid(issuedCoupon)) {
            throw new Exception("Invalid coupon type.");
        }

        Order order = getOrderById(orderId);
        order.applyCouponToOrder(issuedCoupon);
    }

    @Override
    @Transactional
    public Order addOrderItem(Order order, Product product, int quantity) throws Exception {
        OrderItem orderItem = new OrderItem(order, product, quantity);
        order.getItems().add(orderItem);
        orderItemRepository.save(orderItem);
        orderRepository.save(order);
        return order;
    }

    @Override
    public Map<Product, Integer> generateEntry(Long orderId) throws Exception {
        Order orderById = getOrderById(orderId);
        HashMap<Product, Integer> result = new HashMap<>();
        orderById.getItems().forEach(item -> {
            result.put(item.getProduct(), item.getQuantity());
        });
        return result;
    }

    @Override
    @Transactional
    public void standbyOrder(Long orderId) throws Exception {
        Order orderById = getOrderById(orderId);
        orderById.standbyOrder();
        orderRepository.save(orderById);
    }

    @Override
    @Transactional
    public void undoOrder(Long orderId) throws Exception {
        Order orderById = getOrderById(orderId);
        orderById.undoOrder();
        orderRepository.save(orderById);
    }

    @Override
    @Transactional
    public void completeOrder(Long orderId) throws Exception {
        Order orderById = getOrderById(orderId);
        orderById.completeOrder();
        orderRepository.save(orderById);
    }
}
