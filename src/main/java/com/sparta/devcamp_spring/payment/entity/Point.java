package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @Column
    private double availableAmount;

    @OneToMany(mappedBy = "point")
    private List<PointLog> logs;

    @Column
    private LocalDate expirationDate;

    public void use(Double amountToUse) {
        if (this.availableAmount >= amountToUse){
            this.availableAmount -= amountToUse;
            logs.add(PointLog.use(this,amountToUse,"포인트 사용"));
        } else {
            throw new IllegalArgumentException("사용 가능한 포인트가 부족합니다.");
        }
    }

}