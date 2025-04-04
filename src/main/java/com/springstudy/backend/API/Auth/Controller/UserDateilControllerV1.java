package com.springstudy.backend.API.Auth.Controller;

import com.springstudy.backend.API.Auth.Model.Request.ChangeEmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.RetrieveRequest;
import com.springstudy.backend.API.Auth.Model.Response.RetrieveAIResponse;
import com.springstudy.backend.API.Auth.Model.Response.RetrieveTicketResponse;
import com.springstudy.backend.API.Auth.Model.RetrieveResponse;
import com.springstudy.backend.API.Auth.Model.RetrieveType;
import com.springstudy.backend.API.Auth.Service.DetailType;
import com.springstudy.backend.API.Auth.Service.emailTemplate.EmailType;
import com.springstudy.backend.API.Auth.Model.Request.ChangeDetailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Response.ChangeDetailResponse;
import com.springstudy.backend.API.Auth.Service.DetailService;
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
    private final DetailService DetailService;
    private final DetailService detailService;

    @PostMapping("/findPassword")
    public ErrorCode sendEmail(@RequestBody @Valid EmailRequest emailRequest) {
        return emailService.sendMail(emailRequest, EmailType.findPassword);
    }
    @PostMapping("/change/email")
    public ChangeDetailResponse changeEmail(
            @RequestBody ChangeEmailRequest changeEmailRequest){
        return DetailService.changeEmail(changeEmailRequest);
    }
    @PostMapping("/change/password")
    public ChangeDetailResponse changePassword(
            @RequestBody ChangeDetailRequest changeDetailRequest){
        return DetailService.changeDetail(changeDetailRequest,DetailType.PASSWORD);
    }
    @PostMapping("/change/username")
    public ChangeDetailResponse changeUsername(
            @RequestBody ChangeDetailRequest changeDetailRequest){
        return DetailService.changeDetail(changeDetailRequest, DetailType.USERNAME);
    }

    @PostMapping("/retrieve/ticket")
    public RetrieveResponse retrieveTicket(RetrieveRequest lookupTicketRequest) {
        return detailService.retrieve(lookupTicketRequest, RetrieveType.TICKET);
    }
    @PostMapping("/retrieve/AI")
    public RetrieveResponse retrieveAI(RetrieveRequest lookupTicketRequest) {
        return detailService.retrieve(lookupTicketRequest, RetrieveType.AI);
    }
}
