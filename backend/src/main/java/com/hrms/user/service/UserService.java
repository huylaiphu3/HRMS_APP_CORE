package com.hrms.user.service;

import com.hrms.user.entity.User;
import com.hrms.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    public Optional<User> findByUsername(String username){
        return userRepository.findByUsername(username);
    }
}
