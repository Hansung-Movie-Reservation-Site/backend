package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.Request.CreateUserRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailVerifyRequest;
import com.springstudy.backend.API.Auth.Model.Request.LoginRequest;
import com.springstudy.backend.API.Auth.Model.Response.CreateUserResponse;
import com.springstudy.backend.API.Auth.Model.Response.LoginResponse;
import com.springstudy.backend.API.Auth.Service.AuthService;
import com.springstudy.backend.API.Auth.Service.EmailService;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class AuthControllerV1 {
    private final AuthService authService;
    private final EmailService emailService;

    @PostMapping("/createUser")
    public CreateUserResponse CreateUser(@RequestBody CreateUserRequest createUserRequest) {
        return authService.createUser(createUserRequest);
    }
    @PostMapping("/login")
    public LoginResponse Login(@RequestBody LoginRequest loginRequest, HttpServletResponse httpServletResponse
    , HttpServletRequest httpServletRequest) {
        System.out.println(httpServletRequest.getCookies());
        return authService.login(httpServletResponse, loginRequest);
    }
    @PostMapping("/VerifyEmail")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, "VerifyEmail");
    }
    @PostMapping("/check")
    public ErrorCode checkEmail(@RequestBody @Valid EmailVerifyRequest emailRequest) {
        return emailService.CheckAuthNum(emailRequest);
    }
}
