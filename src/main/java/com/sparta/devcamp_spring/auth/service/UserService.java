package com.sparta.devcamp_spring.auth.service;

import com.sparta.devcamp_spring.auth.dto.CreateUserDto;
import com.sparta.devcamp_spring.auth.dto.SignupResponseDto;
import com.sparta.devcamp_spring.auth.entity.User;
import com.sparta.devcamp_spring.auth.entity.UserRole;
import com.sparta.devcamp_spring.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // ADMIN_TOKEN
    private final String ADMIN_TOKEN = "AAABnvxRVklrnYxKZ0aHgTBcXukeZygoC";

    public SignupResponseDto signup (CreateUserDto dto){
        Optional<User> user = userRepository.findByEmail(dto.getEmail());
        if(user.isPresent()){
            throw new RuntimeException(dto.getEmail() + "already exist");
        }

        // 사용자 ROLE 확인
        UserRole role = UserRole.USER;
        if (dto.isAdmin()) {
            if (!ADMIN_TOKEN.equals(dto.getAdminToken())) {
                throw new IllegalArgumentException("관리자 암호가 틀려 등록이 불가능합니다.");
            }
            role = UserRole.ADMIN;
        }

        User createUser = new User(dto, role);
        userRepository.save(createUser);

        return new SignupResponseDto(createUser);
    }

}
