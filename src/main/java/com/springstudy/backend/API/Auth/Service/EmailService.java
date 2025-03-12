package com.springstudy.backend.API.Auth.Service;

import com.springstudy.backend.API.Auth.Model.EmailType;
import com.springstudy.backend.API.Auth.Model.Request.EmailRequest;
import com.springstudy.backend.API.Auth.Model.Request.EmailVerifyRequest;
import com.springstudy.backend.API.Auth.Service.emailTemplate.TemporaryPasswordEmail;
import com.springstudy.backend.API.Auth.Service.emailTemplate.VerifyEmail;
import com.springstudy.backend.API.Repository.Entity.User;
import com.springstudy.backend.API.Repository.UserRepository;
import com.springstudy.backend.Common.ErrorCode.CustomException;
import com.springstudy.backend.Common.ErrorCode.ErrorCode;
import com.springstudy.backend.Common.RedisService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.net.ConnectException;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final RedisService redisUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TemporaryPasswordEmail temporaryPasswordEmail;
    private final VerifyEmail verifyEmail;
    private final PlatformTransactionManager platformTransactionManager;

    public int createVerifyNumber(String mail) throws MessagingException {
        int number = (int)(Math.random() * (90000)) + 100000;
        redisUtil.setDataExpire(Integer.toString(number),mail,60*2);
        System.out.println(redisUtil.getData(Integer.toString(number))+" "+number);
        return number;
    }

    @Transactional
    public String createTemporaryPassword(String mail) throws MessagingException {
        Optional<User> userOptional = userRepository.findByEmail(mail);
        if(userOptional.isEmpty()) {
            throw new CustomException(ErrorCode.NOT_EXIST_USER);
        }
        String temporaryPassword = UUID.randomUUID().toString().replace("-", "").substring(0,8);

        User user = userOptional.get();
        user.getUser_credentional().changePassword(passwordEncoder.encode(temporaryPassword));
        System.out.println(user.getUser_credentional().getPassword());
        userRepository.save(user);

        return temporaryPassword;
    }


    public ErrorCode sendMail(EmailRequest emailRequest, EmailType mode) {
        MimeMessage message = null;
        String email = emailRequest.email();
        try{
            switch(mode){
                case findPassword:
                    String temporaryPassword = createTemporaryPassword(email);
                    message = temporaryPasswordEmail.createEmail(email,"임시 비밀번호 발급",temporaryPassword);
                    break;
                case verifyEmail:
                    int verifyNumber = createVerifyNumber(emailRequest.email());
                    message = verifyEmail.createEmail(email,"이메일 인증번호 발급",verifyNumber);
                    break;
            }
            javaMailSender.send(message);
        }
        catch(MessagingException e){
            //todo error
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.FAILURE);
        }
        catch(MailException e){
            //todo error
            log.error(e.getMessage());
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
            System.out.println(storedEmail==null+" " + !storedEmail.equals(email));
            //throw new CustomException(ErrorCode.ERROR_VERIFY);
            return ErrorCode.VERIFY_FAILED;
        }
        return ErrorCode.SUCCESS;
    }
}