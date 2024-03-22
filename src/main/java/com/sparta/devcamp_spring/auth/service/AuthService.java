package com.sparta.devcamp_spring.auth.service;

import com.sparta.devcamp_spring.auth.entity.RefreshToken;
import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.auth.entity.UserRole;
import com.sparta.devcamp_spring.auth.jwt.JwtProvider;
import com.sparta.devcamp_spring.auth.jwt.TokenType;
import com.sparta.devcamp_spring.auth.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    public String reissueToken(String refreshToken) {
        // 토큰 앞의 'Bearer ' 제거
        refreshToken = refreshToken.substring(7);

        // 리프레시 토큰 유효성 검사
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid Token");
        }

        // Jwt Claims에서 사용자 정보 추출
        Claims claims = jwtProvider.getUserInfoFromToken(refreshToken);
        String userEmail = claims.getSubject();

        // 저장된 리프레시 토큰 조회
        Optional<RefreshToken> storedRefreshToken = refreshTokenService.getRefreshToken(userEmail);
        if (!storedRefreshToken.isPresent()) {
            throw new RuntimeException("Not Found Refresh Token");
        }

        String storedToken = storedRefreshToken.get().getToken();
        // 저장된 토큰과 입력받은 토큰 일치 검사
        if (!storedToken.equals(refreshToken)) {
            throw new RuntimeException("Token Mismatch");
        }

        // 사용자 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Not Found User By: " + userEmail));

        // 새로운 액세스 토큰 생성
        String newAccessToken = jwtProvider.createToken(jwtProvider.createTokenPayload(user.getEmail(), user.getRole(), TokenType.ACCESS));

        return newAccessToken;
    }
}
