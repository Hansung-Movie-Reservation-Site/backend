package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.CreateUserRequest;
import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.DeleteAccountRequest;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.CreateUserResponse;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.DeleteAccountResponse;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.API.Repository.MyTheaterRepository;
import com.springstudy.backend.API.Repository.ReviewLikeRepository;
import com.springstudy.backend.API.Repository.ReviewRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.LoginRequest;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.LoginResponse;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.Entity.UserCredentional;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Security.Password.Hasher;
import com.springstudy.backend.Security.RedisService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import com.springstudy.backend.Security.JWT.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j //
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;
    private final ReviewLikeRepository reviewLikeRepository;
    private final MyTheaterRepository myTheaterRepository;

    public CreateUserResponse createUser(CreateUserRequest request) {
        // 1. 동일 이메일 있나 확인.
        // 2. 비밀번호 암호화.
        // 3. User에 추가.
        // 4. 성공 코드 반환.
        System.out.println("email: "+request.email()+" username: "+request.username()+"password: "+request.password()
                );

        Optional<User> user = userRepository.findByEmail(request.email());
        if(user.isPresent()) {
            log.error("USER_ALREADY_EXISTS: {}");
            return new CreateUserResponse(ErrorCode.USER_ALREADY_EXISTS);
        }
        String encodedPassword = Hasher.hash(request.password());

        try{
            User newUser = User.builder()
                    .email(request.email())
                    .username(request.username())
                    .ordersList(new ArrayList<>()) // ✅ 기본값 설정
                    .build();
            UserCredentional userCredentional = UserCredentional.builder()
                    .user(newUser)
                    .password(encodedPassword)
                    .build();
            newUser.setUserCredentional(userCredentional);
            User savedUser = userRepository.save(newUser);

            if(savedUser == null) {
                log.error("USER_CREATE_FAILED: {}+ null 값 저장 오류");
                return new CreateUserResponse(ErrorCode.USER_CREATE_FAILED);
            }
        }
        catch(Exception e) {
            log.error("USER_CREATE_FAILED: "+e.getMessage());
            return new CreateUserResponse(ErrorCode.USER_CREATE_FAILED);
        }

        return new CreateUserResponse(ErrorCode.SUCCESS);
    }

    public LoginResponse login(HttpServletResponse response, LoginRequest request) {
        // 1. user 레포에 있나 확인.
        // 2. usernamePasswordAuthentication 생성
        // 3. authenticationProvier 생성 및 비밀번호 검증.
        // 4. SecurityContextHolder에 로그인 정보 저장.
        // 5. SecurityContextHolder에서 정보 가져와서 jwt 발급.

        Optional<User> userOptional = userRepository.findByEmail(request.email());
        System.out.println("email: "+request.email()+"user: "+userOptional.isPresent());
        if(userOptional.isEmpty()) {
            //todo error
            log.error("유저 정보가 존재하지 않습니다.");
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
            authUser(userOptional.get().getUsername(),request.password());

        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth == null){
                //todo error
                throw new CustomException(ErrorCode.AUTH_SAVE_ERROR);
            }
            AuthUser user = (AuthUser) auth.getPrincipal();
            String jwt = JWTUtil.createToken(user);
            String refreshJwt = JWTUtil.createRefreshToken(user);
            redisService.setDataExpire("refresh_token: "+((AuthUser)auth.getPrincipal()).getUsername(), refreshJwt, 3600000);
            Cookie cookie= JWTUtil.createCookie("jwt",jwt);
            Cookie refreshCookie= JWTUtil.createCookie("refreshJwt",refreshJwt);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        }
        catch(JwtException e) {
            //todo error
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
        }

        User user = userOptional.get();
        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .user_id(user.getId())
                .myTheaterList(user.getMyTheaterList())
                .build();
        return new LoginResponse(ErrorCode.SUCCESS,userDetailDTO);
    }
    private void authUser(String username, String password){
        try{
            var authentication = new UsernamePasswordAuthenticationToken(username,password);
            Authentication auth = authenticationManagerBuilder.getObject().authenticate(authentication);
            SecurityContextHolder.getContext().setAuthentication(auth);
            // 인증 정보 확인
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            System.out.println("Authentication after setting: " + currentAuth);
        }
        catch(AuthenticationException e) {
        //todo error
        log.error(e.getMessage());
        throw new CustomException(ErrorCode.MISMATCH_PASSWORD);
    }
    }
    @Transactional
    public DeleteAccountResponse deleteAccount(DeleteAccountRequest deleteAccountRequest) {
        String password = deleteAccountRequest.password();
        Optional<User> userOptional = userRepository.findByEmail(deleteAccountRequest.email());
        if(userOptional.isEmpty()){
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        User user = userOptional.get();
        Hasher.checkPassword(user, password);

        reviewLikeRepository.deleteByUserId(user.getId());
        myTheaterRepository.deleteByUser_Id(user.getId());
        //reviewRepository.deleteByUserId(user.getId());    // 추가 FK 관계도 삭제 필요
        userRepository.delete(user);
        return new DeleteAccountResponse(ErrorCode.SUCCESS);

    }
}
