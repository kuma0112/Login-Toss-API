package com.sparta.devcamp_spring.auth.controller;

import com.sparta.devcamp_spring.auth.dto.CreateUserDto;
import com.sparta.devcamp_spring.auth.dto.SignupResponseDto;
import com.sparta.devcamp_spring.auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<SignupResponseDto> signup(@RequestBody CreateUserDto createUserDto) {
        SignupResponseDto responseDto = userService.signup(createUserDto);
        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }
}
