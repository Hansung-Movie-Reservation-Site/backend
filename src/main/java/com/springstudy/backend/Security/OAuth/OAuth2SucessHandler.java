package com.springstudy.backend.Security.OAuth;

import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.util.JsonResponseUtil;
import com.springstudy.backend.Common.util.LogUtil;
import com.springstudy.backend.Security.JWT.JWTUtil;
import com.springstudy.backend.Security.RedisService;
import io.jsonwebtoken.JwtException;
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
    private final JsonResponseUtil jsonResponseUtil;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication){

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();
        AuthUser user = principalDetails.toAuthUser();
        String loginResponse;

        try {
            String jwt = JWTUtil.createToken(user);
            String refreshJwt = JWTUtil.createRefreshToken(user);
            redisService.setDataExpire("refresh_token: " + user.getUsername(), refreshJwt, 3600000);
            Cookie cookie = JWTUtil.createCookie("jwt", jwt);
            Cookie refreshCookie = JWTUtil.createCookie("refreshJwt", refreshJwt);
            response.addCookie(cookie);
            response.addCookie(refreshCookie);

            response.setContentType("application/json; charset=UTF-8");
            Long id = principalDetails.getUser().getId();
            loginResponse = writeSucessResponse(id);

        } catch (JwtException e) {
            LogUtil.error(getClass(),"JwtException 54Line", e);
            loginResponse = jsonResponseUtil.error(500,ErrorCode.JWT_CREATE_ERROR);
        }
        try{
            response.getWriter().write(loginResponse);
            response.sendRedirect("https://cinemagix-xi.vercel.app"); //sendRedirect 문제
        }
        catch(IOException e){
            LogUtil.error(getClass(),"IOException 65Line", e);
            throw new CustomException(ErrorCode.IOEXCEPTION);
        }
    }
    public String writeSucessResponse(Long id){
        String response;
        Optional<User> userOptional = userRepository.findById(id);
        User user = userOptional.get();
        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .email(user.getEmail())
                .username(user.getUsername())
                .user_id(id)
                .myTheaterList(user.getMyTheaterList())
                .build();
        response = jsonResponseUtil.success(userDetailDTO);
        return response;
    }
}
