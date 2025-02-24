package com.springstudy.backend.API.Auth.Service;
import com.springstudy.backend.API.Auth.Model.Request.CreateUserRequest;
import com.springstudy.backend.API.Auth.Model.Response.CreateUserResponse;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.Request.LoginRequest;
import com.springstudy.backend.API.Auth.Model.Response.LoginResponse;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.Entity.UserCredentional;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.Hash.Hasher;
import com.springstudy.backend.Common.JWTUtil;
import com.springstudy.backend.Common.RedisService;
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

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j //
public class AuthService {
    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisService redisService;

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

        Optional<User> user = userRepository.findByEmail(request.email());
        System.out.println("email: "+request.email()+"user: "+user.isPresent());
        if(user.isEmpty()) {
            //todo error
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
            authUser(user.get().getUsername(),request.password());

        try{
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if(auth == null){
                //todo error
                throw new CustomException(ErrorCode.AUTH_SAVE_ERROR);
            }
            String jwt = JWTUtil.createToken(auth);
            String refreshJwt = JWTUtil.createRefreshToken(auth);
            redisService.setDataExpire("refresh_token: "+((AuthUser)auth.getPrincipal()).getUsername(), refreshJwt, 3600000);
            Cookie cookie= createCookie("jwt",jwt);
            Cookie refreshCookie= createCookie("refreshJwt",refreshJwt);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        }
        catch(JwtException e) {
            //todo error
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
        }

        return new LoginResponse(ErrorCode.SUCCESS);
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
    private Cookie createCookie(String name, String jwt){
        Cookie cookie = new Cookie(name, jwt);
        cookie.setHttpOnly(true);   // XSS 공격 방지
        cookie.setSecure(true);     // HTTPS 환경에서만 쿠키 전달 -> 배포시 true 해야 됨.
        cookie.setPath("/");        // 전체 경로에서 쿠키 사용 가능
        cookie.setMaxAge(1000000); // 1일
        cookie.setAttribute("SameSite", "None");  // 크로스 사이트 요청 허용
        return cookie;
    }
}
