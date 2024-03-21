package com.sparta.devcamp_spring.auth.service;

import com.sparta.devcamp_spring.auth.dto.CreateUserDto;
import com.sparta.devcamp_spring.auth.dto.SignupResponseDto;
import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public SignupResponseDto signup (CreateUserDto dto){
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if(user.isPresent()){
            throw new RuntimeException(dto.getEmail() + "already exist");
        }

        User createUser = new User(dto);
        userRepository.save(createUser);

        return new SignupResponseDto(createUser);
    }
}
