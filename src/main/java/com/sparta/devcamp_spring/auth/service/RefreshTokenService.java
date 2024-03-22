package com.sparta.devcamp_spring.auth.service;

import com.sparta.devcamp_spring.auth.entity.RefreshToken;
import com.sparta.devcamp_spring.auth.jwt.JwtProvider;
import com.sparta.devcamp_spring.auth.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RefreshTokenService {
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;

//    public String saveRefreshToken(String keyEmail) {
//        String refreshToken = jwtProvider.createRefreshToken(keyEmail);
//        Optional<RefreshToken> findToken = refreshTokenRepository.findById(keyEmail);
//        if (findToken.isPresent()){
//            findToken.get().updateToken(refreshToken);
//            refreshTokenRepository.save(findToken.get());
//        } else {
//            refreshTokenRepository.save(new RefreshToken(keyEmail, refreshToken));
//        }
//        return refreshToken;
//    }

    public void removeRefreshToken(String keyEmail) {
        refreshTokenRepository.findById(keyEmail)
                .ifPresent(refreshTokenRepository::delete);
    }

    public Optional<RefreshToken> getRefreshToken(String keyEmail) {
        return refreshTokenRepository.findById(keyEmail);
    }
}