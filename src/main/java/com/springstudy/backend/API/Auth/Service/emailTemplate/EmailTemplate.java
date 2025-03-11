package com.springstudy.backend.API.Auth.Service.emailTemplate;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
public abstract class EmailTemplate {
    protected final JavaMailSender javaMailSender;
    protected static final String senderEmail= "verify0213@gmail.com";

    protected abstract String createBody(Object content);

    public MimeMessage createEmail(String mail, String subject, Object content) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        System.out.println(mail);
        message.setSubject(subject);
        message.setText(createBody(content),"UTF-8", "html");

        return message;
    }
}