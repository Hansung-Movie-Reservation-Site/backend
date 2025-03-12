package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Service.DetailType;
import com.springstudy.backend.API.Auth.Service.emailTemplate.EmailType;
import com.springstudy.backend.API.Auth.Model.Request.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Service.ChangeDetailService;
import com.springstudy.backend.API.Auth.Service.EmailService;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/detail")
@RequiredArgsConstructor
public class UserDateilControllerV1 {
    private final EmailService emailService;
    private final ChangeDetailService changeDetailService;

    @PostMapping("/findPassword")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, EmailType.findPassword);
    }
    @PostMapping("/change/email")
    public ChangeDetailResponse changeEmail(
            @RequestBody ChangeDetailRequest changeDetailRequest
    ){
        return changeDetailService.changeDetail(changeDetailRequest, DetailType.EMAIL);
    }
    @PostMapping("/change/password")
    public ChangeDetailResponse changePassword(
            @RequestBody ChangeDetailRequest changeDetailRequest
    ){
        return changeDetailService.changeDetail(changeDetailRequest,DetailType.PASSWORD);
    }
    @PostMapping("/change/username")
    public ChangeDetailResponse changeUsername(
            @RequestBody ChangeDetailRequest changeDetailRequest
    ){
        return changeDetailService.changeDetail(changeDetailRequest, DetailType.USERNAME);
    }
}
