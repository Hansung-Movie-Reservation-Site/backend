package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailVerifyRequest;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Autowired
    private final JavaMailSender javaMailSender;
    private static final String senderEmail= "verify0213@gmail.com";
    private int number;
    private final RedisService redisUtil;
    private final UserRepository userRepository;

    // 랜덤으로 숫자 생성
    //@Bean
    public static int createNumber() {
        return (int)(Math.random() * (90000)) + 100000; //(int) Math.random() * (최댓값-최소값+1) + 최소값
    }

    public MimeMessage CreateMail(String mail) throws MessagingException {
        number = createNumber();
        redisUtil.setDataExpire(Integer.toString(number),mail,60*2);
        System.out.println(redisUtil.getData(Integer.toString(number))+" "+number);
        MimeMessage message = javaMailSender.createMimeMessage();

            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, mail);
            System.out.println(mail);
            message.setSubject("이메일 인증");
            String body = "";
            body += "<h3>" + "요청하신 인증 번호입니다." + "</h3>";
            body += "<h1>" + number + "</h1>";
            body += "<h3>" + "감사합니다." + "</h3>";
            message.setText(body,"UTF-8", "html");

        return message;
    }

    public ErrorCode sendMail(EmailRequest emailRequest) {
        try{
            MimeMessage message = CreateMail(emailRequest.email());
            javaMailSender.send(message);
        }
        catch(MessagingException e){
            //todo error
            throw new CustomException(ErrorCode.FAILURE);
        }
        catch(MailException e){
            //todo error
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        }

        return ErrorCode.SUCCESS;
    }

    //추가 되었다.
    public ErrorCode CheckAuthNum(EmailVerifyRequest emailRequest){
        String authNum = emailRequest.authnum();
        String email = emailRequest.email();
        String storedEmail = redisUtil.getData(authNum);
        userRepository.findByEmail(email).ifPresent(user -> {
            throw new CustomException(ErrorCode.USER_ALREADY_EXISTS);
        });

        if(storedEmail==null || !storedEmail.equals(email)){
            //인증번호 틀림.
            //todo error
            //throw new CustomException(ErrorCode.ERROR_VERIFY);
            return ErrorCode.VERIFY_FAILED;
        }
        return ErrorCode.SUCCESS;
    }
}