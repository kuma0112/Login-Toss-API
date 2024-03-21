package com.sparta.devcamp_spring.auth.dto;

import com.sparta.devcamp_spring.auth.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;

    public SignupResponseDto(Long id, String name, String email, String phoneNumber){
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public SignupResponseDto(User user){
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.phoneNumber = user.getPhoneNumber();
    }
}
