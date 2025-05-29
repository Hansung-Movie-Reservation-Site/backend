package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.AuthUser;
import com.springstudy.backend.API.Auth.Model.PrincipleUser;
import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.CreateUserRequest;
import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.DeleteAccountRequest;
import com.springstudy.backend.API.Auth.Model.Request.AccountRequest.LoginRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest.EmailVerifyRequest;
import com.springstudy.backend.API.Auth.Model.UserDetailDTO;
import com.springstudy.backend.API.Auth.Service.emailTemplate.EmailType;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.CreateUserResponse;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.DeleteAccountResponse;
import com.springstudy.backend.API.Auth.Model.Response.AccountResponse.LoginResponse;
import com.springstudy.backend.API.Auth.Service.AuthService;
import com.springstudy.backend.API.Auth.Service.EmailService;
import com.springstudy.backend.API.Repository.Entity.MyTheater;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.MyTheaterRepository;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Security.OAuth.PrincipalDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/user")
@RequiredArgsConstructor
public class AuthControllerV1 {
    private final AuthService authService;
    private final EmailService emailService;
    private final MyTheaterRepository myTheaterRepository;
    private final UserRepository userRepository;

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
    @PostMapping("/verifyEmail")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, EmailType.VERIFYEMAIL);
    }
    @PostMapping("/check")
    public ErrorCode checkEmail(@RequestBody @Valid EmailVerifyRequest emailRequest) {
        return emailService.CheckAuthNum(emailRequest);
    }
    @PostMapping("/deleteAccount")
    public DeleteAccountResponse DeleteAccount(
            @RequestBody @Valid DeleteAccountRequest deleteAccountRequest) {
        return authService.deleteAccount(deleteAccountRequest);
    }
    @PostMapping("/me")
    public ResponseEntity myData(@AuthenticationPrincipal PrincipleUser principal) {
        System.out.println("principal: "+principal);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object principal1 = auth.getPrincipal();

        System.out.println("auth: " + auth);
        System.out.println("principal: " + principal1);
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        //AuthUser authUser = principal.getUsername();
        User user = userRepository.findByUsername(principal.getUsername()).get();
        List<MyTheater> myTheater = myTheaterRepository.findByUserId(user.getId());
        UserDetailDTO userDetailDTO = UserDetailDTO.builder()
                .user_id(user.getId())
                .email(user.getEmail())
                .myTheaterList(myTheater)
                .username(user.getUsername())
                .build();
        return ResponseEntity.ok(userDetailDTO);
    }
}
