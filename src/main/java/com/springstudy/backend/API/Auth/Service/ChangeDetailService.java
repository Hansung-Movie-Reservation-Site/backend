package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.Request.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Request.DeleteAccountRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Model.Response.DeleteAccountResponse;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChangeDetailService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ChangeDetailResponse changeDetail(ChangeDetailRequest changeDetailRequest) {
        // 사용자 정보 변경.
        // 1. 사용자 유무 확인.
        // 2. 비밀번호 확인.
        // 3. change 함수로 변경.

        String email = changeDetailRequest.email();

        Optional<User> userOptional = userRepository.findByEmail(email);
        if(userOptional.isEmpty()){
            //todo
            log.error("User not found");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();

        String before = changeDetailRequest.password();
        checkPassword(user, before);
        // 원래 정보가 맞는지 비밀번호로 확인한다.
        changeDetail(user, changeDetailRequest);
        return new ChangeDetailResponse(ErrorCode.SUCCESS);
    }

    private void checkPassword(User user, String password) {
        if(!passwordEncoder.matches(password,user.getUser_credentional().getPassword())){
            throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
        }
    }
    private void changeDetail(User user, ChangeDetailRequest changeDetailRequest) {
        String object = changeDetailRequest.object();
        String after = changeDetailRequest.after();
        String before = changeDetailRequest.password();

        switch (object) {
            case "username": user.changeUsername(after);break;
            case "password": user.getUser_credentional().changePassword(passwordEncoder.encode(before));break;
            case "email": user.changeEmail(after);break;
        }
    }

    private DeleteAccountResponse deleteAccount(DeleteAccountRequest deleteAccountRequest) {
        String password = deleteAccountRequest.password();
        Optional<User> userOptional = userRepository.findByEmail(deleteAccountRequest.email());
        if(userOptional.isEmpty()){
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();
        checkPassword(user, password);
        userRepository.delete(user);

        return new DeleteAccountResponse();

    }
}