package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
public class IssuedCoupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @OneToOne
    private Order usedOrder;

    @Column(columnDefinition = "boolean default false")
    private boolean isValid;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date validFrom;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date validUntil;

    @Column(columnDefinition = "boolean default false")
    @Setter
    private boolean isUsed;

    @Column(nullable = true)
    @Temporal(TemporalType.TIMESTAMP)
    private Date usedAt;

    public void use() {
        this.isUsed = true;
        this.isValid = false;
        this.usedAt = new Date();
    }

    public IssuedCoupon (User user, Coupon coupon) {
        this.user = user;
        this.coupon = coupon;
        this.validFrom = new Date();
        this.validUntil = new Date(System.currentTimeMillis()+ (1000L * 60 * 60 * 24 * 7)); // 유효기간 1주일
        this.isUsed = false;
        this.isValid = true;
    }
}