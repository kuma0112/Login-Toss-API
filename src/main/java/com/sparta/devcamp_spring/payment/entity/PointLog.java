package com.sparta.devcamp_spring.payment.entity;

import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class PointLog extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "point_id")
    private Point point;

    @Column(columnDefinition = "int default 0")
    private int amount; // 개별 적립 또는 사용 금액

    @Column(length = 1000)
    private String reason; // 개별 적립 또는 사용 사유

    @Column(length = 50)
    private String type; // PointLogType를 String으로 대체

    public PointLog(Point point, int amount, String reason, String type) {
        this.point = point;
        this.amount = amount;
        this.reason = reason;
        this.type = type;
    }

    public static PointLog use(Point point, int amount, String reason) {
        return new PointLog(point, amount, reason, "use");
    }

    public static PointLog add(Point point, int amount, String reason) {
        return new PointLog(point, amount, reason, "earn");
    }
}
