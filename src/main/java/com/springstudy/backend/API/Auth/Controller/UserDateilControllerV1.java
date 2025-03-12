package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.EmailType;
import com.springstudy.backend.API.Auth.Model.Request.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Request.DeleteAccountRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Model.Response.DeleteAccountResponse;
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

    @PostMapping("/change")
    public ChangeDetailResponse changeDetail(
            @RequestBody ChangeDetailRequest changeDetailRequest
    ){
        return changeDetailService.change(changeDetailRequest);
    }
    @PostMapping("/findPassword")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, EmailType.findPassword);
    }
}
