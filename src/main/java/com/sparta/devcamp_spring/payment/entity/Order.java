package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Table(name = "ORDER_ENTRY")
public class Order extends BaseEntity {
    public Order(User user, List<OrderItem> items, ShippingInfo shippingInfo) {
        this.user = user;
        this.amount = getCheckoutPrice();
        this.items = items;
        this.shippingInfo = shippingInfo;
        setOrderNumber();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 255)
    private String orderNumber;

    @Column
    private Double amount;

    @Column(length = 100)
    private String status;

    @OneToMany(mappedBy = "order")
    @Column
    private List<OrderItem> items;

    @Column(columnDefinition = "double default 0")
    private Double pointAmountUsed;

    @OneToOne(mappedBy = "usedOrder")
    private IssuedCoupon usedIssuedCoupon;

    @OneToOne(mappedBy = "order")
    private ShippingInfo shippingInfo;

    @Column(length = 1000)
    private String refundReason;

    @Column(precision = 10, scale = 2)
    private BigDecimal refundedAmount;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date refundedAt;

//    @Column
//    private Object pgMetadata;

    public Order() {
        setOrderNumber();
    }

    private void setOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String dateFormat = now.format(formatter);

        Random random = new Random();
        StringBuilder randomString = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            char randomChar = (char) ('A' + random.nextInt(26));
            randomString.append(randomChar);
        }
        this.orderNumber = dateFormat + "_" + randomString.toString();
    }

    public double getCheckoutPrice() {
        double amount = items.stream().mapToDouble(OrderItem::getEntryPrice).sum();
        amount -= this.pointAmountUsed;

        Coupon coupon = this.usedIssuedCoupon.getCoupon();
        if (coupon != null) {
            if (coupon.getCouponType().equalsIgnoreCase("PERCENT-OFF")) {
                amount *= (1 - coupon.getAmount());
            } else if (coupon.getCouponType().equalsIgnoreCase("FIXED-AMOUNT-OFF")) {
                amount -= coupon.getAmount();
            }

        }
        this.amount = amount;
        return amount;
    }

    public void applyPointToOrder(Double point) {
       this.pointAmountUsed = point;
       this.amount = getCheckoutPrice();
    }

    public void applyCouponToOrder(IssuedCoupon issuedCoupon) {
        this.usedIssuedCoupon = issuedCoupon;
        this.amount = getCheckoutPrice();
    }

    public void standbyOrder() {
        this.status = "STAND_BY";
    }

    public void undoOrder() {
        this.status = "READY";
    }

    public void completeOrder() {
        this.status = "COMPLETED";
    }

}
