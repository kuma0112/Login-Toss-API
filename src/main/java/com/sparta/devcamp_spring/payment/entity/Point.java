package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


import java.util.List;

@Entity
@Getter
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column
    private int availableAmount;

    @OneToMany(mappedBy = "point")
    private List<PointLog> logs;

    public void use(Double amountToUse) {
        this.availableAmount -= amountToUse;
    }

}