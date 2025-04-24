package com.springstudy.backend.Security.OAuth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.LoginResponse;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.util.LogUtil;
import com.springstudy.backend.Security.JWT.JWTUtil;
import com.springstudy.backend.Security.RedisService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            Authentication authentication){

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        AuthUser user = principalDetails.toAuthUser();

        try {
            String jwt = JWTUtil.createToken(user);
            String refreshJwt = JWTUtil.createRefreshToken(user);
            redisService.setDataExpire("refresh_token: " + user.getUsername(), refreshJwt, 3600000);
            Cookie cookie = JWTUtil.createCookie("jwt", jwt);
            Cookie refreshCookie = JWTUtil.createCookie("refreshJwt", refreshJwt);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);
        } catch (JwtException e) {
            LogUtil.error(getClass(),"JwtException 54 line", e);
            throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
        }

        try{
            response.setContentType("application/json; charset=UTF-8");
            Long id = principalDetails.getUser().getId();
            String loginResponse = writeLoginResponse(id);
            response.getWriter().write(loginResponse);
        }
        catch(IOException e){
            LogUtil.error(getClass(),"IOException 65line", e);
            throw new CustomException(ErrorCode.JWT_CREATE_ERROR);
        }
        response.setStatus(200);
        //response.sendRedirect("http://localhost:3000/"); sendRedirect 문제
    }
    public String writeLoginResponse(Long id){
        String response;
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .user_id(id)
                .myTheatherList(user.getMyTheatherList())
                .build();
        LoginResponse loginResponse = new LoginResponse(ErrorCode.SUCCESS, userDetailDTO);
        try{
             response = objectMapper.writeValueAsString(loginResponse);
        }
        catch(JsonProcessingException e){
            throw new CustomException(ErrorCode.JSON_PROCESSOR_ERROR);
        }
        return response;
    }
}
