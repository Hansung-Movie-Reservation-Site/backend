package com.springstudy.backend.Common.util;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserUtil {
    private final UserRepository userRepository;

    public User existUserByEmail(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()) {
            LogUtil.error("UserUtil", "NOT_EXIST_USER");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        else return userOptional.get();
    }

    public User existUserById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        if(userOptional.isEmpty()) {
            LogUtil.error("UserUtil", "NOT_EXIST_USER");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        else return userOptional.get();
    }
}
