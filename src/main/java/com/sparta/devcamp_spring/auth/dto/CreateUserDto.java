package com.sparta.devcamp_spring.auth.dto;

import com.sparta.devcamp_spring.auth.entity.UserRole;
import lombok.Getter;

@Getter
public class CreateUserDto {
    private String name;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean isAdmin = false;
    private String adminToken = "";
}
