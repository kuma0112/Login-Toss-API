package com.sparta.devcamp_spring.auth.jwt;

import com.sparta.devcamp_spring.auth.entity.UserRole;
import lombok.Getter;

import java.util.Date;

@Getter
public class TokenPayload {
    private String subject; // 사용자의 식별 정보나 ID를 값으로 가진다.
    private String jwtId;
    private Date issuedAt;
    private Date expiresAt;
    private UserRole role;

    public TokenPayload(String subject, String jwtId, Date issuedAt, Date expiresAt, UserRole role) {
        this.subject = subject;
        this.jwtId = jwtId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.role = role;
    }
}
