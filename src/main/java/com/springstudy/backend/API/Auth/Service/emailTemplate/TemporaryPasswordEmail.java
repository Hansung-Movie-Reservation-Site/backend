package com.springstudy.backend.API.Auth.Service.emailTemplate;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class TemporaryPasswordEmail extends EmailTemplate {
    public TemporaryPasswordEmail(JavaMailSender javaMailSender) {
        super(javaMailSender);
    }
    @Override
    public String createBody(Object content) {
        return "<h3>임시 비밀번호 발급</h3><h1>" + content + "</h1><h3>로그인 후 비밀번호를 변경해주세요.</h3>";
    }
}