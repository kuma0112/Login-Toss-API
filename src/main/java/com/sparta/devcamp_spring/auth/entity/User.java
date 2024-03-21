package com.sparta.devcamp_spring.auth.entity;

import com.sparta.devcamp_spring.auth.dto.CreateUserDto;
import com.sparta.devcamp_spring.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "users")
@NoArgsConstructor
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String email;

    @Column
    private String password;

    @Column (length = 50)
    private String phoneNumber;

    @Column
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @Column (columnDefinition = "boolean default false")
    private boolean isPersonalInfoVerified;

    public User(String name, String email, String password, String phoneNumber, UserRole role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.role = role;
        this.isPersonalInfoVerified = true;
    }

    public User(CreateUserDto dto){
        this.name = dto.getName();
        this.email = dto.getEmail();
        this.password = dto.getPassword();
        this.phoneNumber = dto.getPhoneNumber();
        this.role = dto.getUserRole();
        this.isPersonalInfoVerified = true;
    }
}
