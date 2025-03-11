package com.springstudy.backend.Common;

import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckPasswordService {
    private final PasswordEncoder passwordEncoder;

    public void checkPassword(User user, String password) {
        if(!passwordEncoder.matches(password,user.getUser_credentional().getPassword())){
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
        }
    }
}
