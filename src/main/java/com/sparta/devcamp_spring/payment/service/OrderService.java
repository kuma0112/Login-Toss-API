package com.sparta.devcamp_spring.payment.service;


import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.payment.entity.*;

import java.util.List;
import java.util.Map;

public interface OrderService {
    /**
     * 주문을 생성한다
     * @param user user entity
     * @param orderItems order item entity
     * @param shippingInfo shippingInfo entity
     * @return created order
     */
    Order createOrder(User user, List<OrderItem> orderItems, ShippingInfo shippingInfo);

    /**
     * 주문을 조회한다
     * @param orderId order entity pk
     * @return order entity
     * @throws Exception exception
     */
    Order getOrderById(Long orderId) throws Exception;

    /**
     * 주문의 결제 가격을 조회한다
     * @param orderId order entity pk
     * @return 주문 결제 가격
     * @throws Exception exception
     */
    Double getOrderCheckoutPrice(Long orderId) throws Exception;

    /**
     * 주문에 포인트를 적용한다
     * @param orderId order entity pk
     * @param point point amount
     * @throws Exception exception
     */
    void applyPointToOrder(Long orderId, Double point) throws Exception;

    /**
     * 주문에 쿠폰을 적용한다
     * @param orderId order entity pk
     * @param issuedCoupon coupon entity
     * @throws Exception exception
     */
    void applyCouponToOrder(Long orderId, IssuedCoupon issuedCoupon) throws Exception;

    /**
     * 상품에 적용할 엔트리를 뽑는다.
     * @param orderId order entity pk
     * @return product, amount entry map
     * @throws Exception exception
     */
    public Map<Product, Integer> generateEntry(Long orderId) throws Exception;

    /**
     * 주문을 standby 상태로 변경한다
     * @param orderId order entity pk
     * @throws Exception exception
     */
    void standbyOrder(Long orderId) throws Exception;

    /**
     * 주문의 대기상태를 취소한다.
     * @param orderId order entity pk
     * @throws Exception exception
     */
    void undoOrder(Long orderId) throws Exception;

    /**
     * 주문을 완료 상태로 저장한다
     * @param orderId order entity pk
     */
    void completeOrder(Long orderId) throws Exception;

    /**
     * 주문에 상품을 추가한다
     * @param order order entity
     * @param product product entity
     * @param quantity amount
     * @return 주문이 추가된 order entity
     * @throws Exception exception
     */
    Order addOrderItem(Order order, Product product, int quantity) throws Exception;
}
