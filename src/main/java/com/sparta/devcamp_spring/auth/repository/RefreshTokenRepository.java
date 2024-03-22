package com.sparta.devcamp_spring.auth.repository;

import com.sparta.devcamp_spring.auth.entity.RefreshToken;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.CrudRepository;

@Configuration
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
