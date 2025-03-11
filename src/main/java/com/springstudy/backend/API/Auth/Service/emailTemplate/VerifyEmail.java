package com.springstudy.backend.API.Auth.Service.emailTemplate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class VerifyEmail extends EmailTemplate {

    public VerifyEmail(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }

    @Override
    protected String createBody(Object content){
        return "<h3>이메일 인증번호 발급</h3><h1>" + content + "</h1><h3>감사합니다.</h3>";
    }
}
