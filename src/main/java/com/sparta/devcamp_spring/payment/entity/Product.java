package com.sparta.devcamp_spring.payment.entity;


import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;


@Entity
@Getter
public class Product extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(length = 255)
    private String name;

    @Column
    private Double price;

    @Column(columnDefinition = "int default 0")
    private int stock;

    @Column(length = 255)
    private String category;

    @Column(length = 1000)
    private String imageUrl;

    @Column(columnDefinition = "text")
    private String description;

    @Column(length = 50)
    private String status = "available";

    public void decreaseStock(Integer newStock) throws Exception {
        if (this.stock - newStock < 0) {
            throw new Exception("Out Of Stock");
        }
        this.stock = this.stock - newStock;
    }

    public void increaseStock(Integer newStock) throws Exception {
        this.stock = this.stock + newStock;
    }
}