package com.springstudy.backend.Security.OAuth;

import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.util.JsonResponseUtil;
import com.springstudy.backend.Common.util.LogUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2FailureHandler implements AuthenticationFailureHandler {

    private final JsonResponseUtil jsonResponseUtil;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception){
        int status;
        String message;
        String loginResponse;

        // 인증 관련 예외 (401)
        if (exception instanceof OAuth2AuthenticationException ||
                exception instanceof BadCredentialsException ||
                exception instanceof AccountExpiredException ||
                exception instanceof DisabledException) {
            status = 400;
            message = "인증에 실패했습니다.";
            response.setStatus(status);
        }
        // 서버 내부 오류 (500)
        else {
            status = 500;
            message = "서버 오류가 발생했습니다.";
            response.setStatus(status);
        }

        response.setContentType("application/json; charset=UTF-8");
        loginResponse = jsonResponseUtil.error(status, message);
        try{
            response.getWriter().write(loginResponse);
        }
        catch(IOException e){
            LogUtil.error(getClass(), exception.getMessage());
            throw new CustomException(ErrorCode.IOEXCEPTION);
        }
        LogUtil.error(getClass(), exception.getMessage());
    }
}
