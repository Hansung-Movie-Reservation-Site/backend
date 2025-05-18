package com.springstudy.backend.API.User;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}