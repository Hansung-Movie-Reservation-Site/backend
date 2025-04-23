package com.springstudy.backend.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.API.OAuth.PrincipalDetails;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.JWTCommon.JWTUtil;
import com.springstudy.backend.Common.RedisService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SucessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepository;
    private final RedisService redisService;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        AuthUser user = principalDetails.toAuthUser();
        //Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
//        AuthUser user = principalDetails.toAuthUser();
//        System.out.println(user.getEmail());

        try {
            String jwt = JWTUtil.createToken(principalDetails);
            String refreshJwt = JWTUtil.createRefreshToken(principalDetails);
            redisService.setDataExpire("refresh_token: " + user.getUsername(), refreshJwt, 3600000);
            Cookie cookie = createCookie("jwt", jwt);
            Cookie refreshCookie = createCookie("refreshJwt", refreshJwt);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        } catch (JwtException e) {
            //todo error
            //log.error(e.getMessage());
            //throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
            System.out.println(e.getMessage());
        }

//        Long id = principalDetails.getUser().getId();
//        Optional<User> userOptional = userRepository.findById(id);
//        User user1 = userOptional.get();
//        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
//                .email(user.getEmail())
//                .username(user.getUsername())
//                .user_id(id)
//                .myTheatherList(user1.getMyTheatherList())
//                .build();
//        String json = objectMapper.writeValueAsString(userDetailDTO);
//
//        response.getWriter().write(json);
        response.sendRedirect("http://localhost:3000/");
    }

//    public LoginResponse login(HttpServletResponse response) {
//        // 1. user 레포에 있나 확인.
//        // 2. usernamePasswordAuthentication 생성
//        // 3. authenticationProvier 생성 및 비밀번호 검증.
//        // 4. SecurityContextHolder에 로그인 정보 저장.
//        // 5. SecurityContextHolder에서 정보 가져와서 jwt 발급.
//
//        try {
//            String jwt = JWTUtil.createToken(auth);
//            String refreshJwt = JWTUtil.createRefreshToken(auth);
//            redisService.setDataExpire("refresh_token: " + ((AuthUser) auth.getPrincipal()).getUsername(), refreshJwt, 3600000);
//            Cookie cookie = createCookie("jwt", jwt);
//            Cookie refreshCookie = createCookie("refreshJwt", refreshJwt);
//            response.addCookie(cookie);
//            response.addCookie(refreshCookie);
//        } catch (JwtException e) {
//            //todo error
//            //log.error(e.getMessage());
//            //throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
//            System.out.println(e.getMessage());
//        }
//
//        User user = userOptional.get();
//        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
//                .email(user.getEmail())
//                .username(user.getUsername())
//                .user_id(user.getId())
//                //.myTheatherList(user.getMyTheatherList())
//                .build();
//        return new LoginResponse(ErrorCode.SUCCESS, userDetailDTO);
//    }
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
