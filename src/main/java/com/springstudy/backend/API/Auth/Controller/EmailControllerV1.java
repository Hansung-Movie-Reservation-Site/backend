package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailVerifyRequest;
import com.springstudy.backend.API.Auth.Service.EmailService;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping ("api/v1/email")
@RequiredArgsConstructor
public class EmailControllerV1 {
    public final EmailService emailService;
    @PostMapping("/send")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, "verifyEmail");
    }
    @PostMapping("/check")
    public ErrorCode checkEmail(@RequestBody @Valid EmailVerifyRequest emailRequest) {
        return emailService.CheckAuthNum(emailRequest);
    }
}
